package il.ac.technion.cs.sd.sub.app;

public class JournalRegistration {
	private boolean isSubscribed;
	private boolean wasSubscribed;
	private boolean wasCanceled;
	private final String journalID;
	private int price;

	public JournalRegistration(boolean isRegistered, boolean wasRegistered, boolean wasCanceled, String journalID,
			int price) {
		this.isSubscribed = isRegistered;
		this.wasSubscribed = wasRegistered;
		this.wasCanceled = wasCanceled;
		this.journalID = journalID;
		this.price = price;
	}

	public JournalRegistration(String journalID) {
		this.isSubscribed = true;
		this.wasSubscribed = true;
		this.wasCanceled = false;
		this.journalID = journalID;
	}

	public JournalRegistration(String journalID, boolean isSunscribed) {
		if (!isSunscribed) {
			this.isSubscribed = false;
			this.wasSubscribed = false;
			this.wasCanceled = true;
		} else {
			this.isSubscribed = true;
			this.wasSubscribed = true;
			this.wasCanceled = false;
		}

		this.journalID = journalID;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isSubscribed ? 1231 : 1237);
		result = prime * result + ((journalID == null) ? 0 : journalID.hashCode());
		result = prime * result + (wasCanceled ? 1231 : 1237);
		result = prime * result + (wasSubscribed ? 1231 : 1237);
		return result;
	}

	public boolean isSubscribed() {
		return isSubscribed;
	}

	public boolean wasSubscribed() {
		return wasSubscribed;
	}

	public boolean wasCanceled() {
		return wasCanceled;
	}

	public String getJournalID() {
		return journalID;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void cancell() {
		this.wasCanceled = true;
		this.isSubscribed = false;
		this.wasSubscribed = true;
	}

	public void reSubscribed() {
		this.wasCanceled = true;
		this.isSubscribed = true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JournalRegistration other = (JournalRegistration) obj;
		if (isSubscribed != other.isSubscribed)
			return false;
		if (journalID == null) {
			if (other.journalID != null)
				return false;
		} else if (!journalID.equals(other.journalID))
			return false;
		if (wasCanceled != other.wasCanceled)
			return false;
		if (wasSubscribed != other.wasSubscribed)
			return false;
		return true;
	}

	public String serialize() {
		String status = isSubscribed() ? "s" : "";
		status += wasSubscribed() ? "w" : "";
		status += wasCanceled() ? "c" : "";
		return getJournalID() + "," + status + "," + getPrice();
	}

	public static JournalRegistration parse(String s) {
		String[] ss = s.split(",");
		if(ss.length!=3)
			return null;
		return new JournalRegistration(ss[1].contains("s"), ss[1].contains("w")//
				, ss[1].contains("c"), ss[0], Integer.parseInt(ss[2]));
	}

}
