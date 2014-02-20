package models;

import javax.persistence.*;

@Entity
@DiscriminatorValue("L")
public class LargeCabin extends Cabin {

	
}
