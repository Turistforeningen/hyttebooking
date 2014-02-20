package models;

import javax.persistence.*;

@Entity
@DiscriminatorValue("S")
public class SmallCabin extends Cabin {

	
}
