package no.westerdals.quiz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.westerdals.quiz.dto.CategoryDto;
import no.westerdals.quiz.dto.SubcategoryDto;
import no.westerdals.quiz.converters.CategoryDtoConverter;
import no.westerdals.quiz.converters.SubcategoryDtoConverter;
import no.westerdals.quiz.ejb.CategoryEJB;
import no.westerdals.quiz.entities.Category;
import no.westerdals.quiz.entities.Subcategory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CategoryRestImpl implements CategoryRest {
    private final ObjectMapper JACKSON = new ObjectMapper();

    @Context
    private UriInfo uriInfo;

    @EJB
    private CategoryEJB categoryEJB;

    private CategoryDtoConverter categoryConverterExpanding = new CategoryDtoConverter(true);
    private CategoryDtoConverter categoryConverter = new CategoryDtoConverter(false);
    private SubcategoryDtoConverter subcategoryConverter = new SubcategoryDtoConverter();

    @Override
    public Response createCategory(CategoryDto categoryDto) {
        Category category = categoryEJB.createCategory(categoryDto.text);
        return Response.created(uriInfo.getBaseUriBuilder().path(ENDPOINT).path(category.getId().toString()).build())
                .build();
    }

    @Override
    public List<CategoryDto> getCategories(boolean expand) {
        if (expand) {
            return categoryConverterExpanding.convert(categoryEJB.getCategoriesWithSubcategories());
        } else {
            return categoryConverter.convert(categoryEJB.getCategories());
        }
    }

    @Override
    public CategoryDto getCategory(Long id, boolean expand) {
        if (expand) {
            return categoryConverterExpanding.convert(categoryEJB.getCategoryWithSubcategories(id));
        } else {
            return categoryConverter.convert(categoryEJB.getCategory(id));
        }
    }

    @Override
    public void deleteCategory(Long id) {
        categoryEJB.deleteCategory(id);
    }

    @Override
    public Response patchCategory(Long id, String jsonString) {
        Category category = categoryEJB.getCategory(id);
        if (category == null) { // In case we want to change its behavior
            throw new WebApplicationException(404);
        }

        JsonNode json;
        try {
            json = JACKSON.readValue(jsonString, JsonNode.class);
        } catch (Exception e) {
            throw new WebApplicationException(/*"Invalid json input", */400);
        }

        String text = json.get("text").asText(category.getText());

        categoryEJB.updateCategory(category.getId(), text);

        return Response.ok().location(uriInfo.getBaseUriBuilder().path(ENDPOINT).path(category.getId().toString()).build())
                .build();
    }

    @Override
    public Response getSubcategoriesByCategory(Long categoryId) {
        return Response.status(301)
                .location(uriInfo.getBaseUriBuilder()
                        .path(ENDPOINT)
                        .path("subcategories")
                        .queryParam("parentId", categoryId)
                        .build())
                .build();
    }

    @Override
    public Response createSubcategory(Long parentId, SubcategoryDto subcategoryDto) {
        Subcategory subcategory = categoryEJB.createSubcategory(parentId, subcategoryDto.text);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path(ENDPOINT)
                        .path("subcategory")
                        .path(subcategory.getId().toString())
                        .build())
                .build();
    }

    @Override
    public List<SubcategoryDto> getSubcategories(Long parentId) {
        if (parentId == null) {
            return subcategoryConverter.convert(categoryEJB.getSubCategories());
        } else {
            return subcategoryConverter.convert(categoryEJB.getSubcategoryByParent(parentId));
        }
    }

    @Override
    public SubcategoryDto getSubcategory(Long id) {
        return subcategoryConverter.convert(categoryEJB.getSubCategory(id));
    }
}
