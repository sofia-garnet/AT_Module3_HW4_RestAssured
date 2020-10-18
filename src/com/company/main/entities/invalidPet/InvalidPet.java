package com.company.main.entities.invalidPet;
import com.company.main.entities.pet.Category;
import com.company.main.entities.pet.Tag;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InvalidPet {
    public double id;
    public Category category;
    public String name;
    public List<String> photoUrls;
    public List<Tag> tags;
    public String status;
}
