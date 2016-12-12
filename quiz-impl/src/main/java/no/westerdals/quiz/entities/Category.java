package no.westerdals.quiz.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = Category.GET_ALL,
                query = "select c from Category as c"
        )
})
@Entity
public class Category {
    public static final String GET_ALL = "Category#getAll";
    @GeneratedValue
    @Id
    private Long id;
    @NotNull
    @Min(4)
    private String text;
    @OneToMany(mappedBy = "parent")
    private List<SubCategory> subcategories;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<SubCategory> getSubcategories() {
        return subcategories;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubcategories(List<SubCategory> subcategories) {
        this.subcategories = subcategories;
    }
}