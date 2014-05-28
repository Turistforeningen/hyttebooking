package JSONFormatters;

public class PriceJson {

	public String guestType;
	public double memberPrice;
	public double nonMemberPrice;
	public boolean isMinor;
	public String ageRange;
	
	public PriceJson(String guestType, double memberPrice, double nonMemberPrice, boolean isMinor, String ageRange) {
		this.guestType = guestType;
		this.memberPrice = memberPrice;
		this.nonMemberPrice = nonMemberPrice;
		this.isMinor = isMinor;
		this.ageRange = ageRange;
	}
}
