package il.ac.technion.cs.sd.sub.app;

public class JournalRegistration {
	private final boolean isSubscribed;
	private final boolean wasSubscribed;
	private final boolean wasCanceled;
	private final String journalID;
	private final int price;

	public JournalRegistration(boolean isRegistered, boolean wasRegistered, boolean wasCanceled, String journalID, int price) {
		this.isSubscribed = isRegistered;
		this.wasSubscribed = wasRegistered;
		this.wasCanceled = wasCanceled;
		this.journalID = journalID;
		this.price = price;
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

}
