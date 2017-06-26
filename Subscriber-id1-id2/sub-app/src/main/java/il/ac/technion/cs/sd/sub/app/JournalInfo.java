package il.ac.technion.cs.sd.sub.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JournalInfo {
	@Override
	public String toString() {
		return "JournalInfo [price=" + price + ", users=" + users + ", wasDeclared=" + wasDeclared + "]";
	}

	private int price;
	private final List<String> users;
	private boolean wasDeclared;

	public JournalInfo(int price, List<String> users) {
		this.price = price;
		this.users = users;
		this.wasDeclared = false;
	}

	public JournalInfo(int price, List<String> users, boolean wasDeclared) {
		this.price = price;
		this.users = users;
		this.wasDeclared = wasDeclared;
	}

	public boolean wasDeclared() {
		return this.wasDeclared;
	}

	public void declare() {
		this.wasDeclared = true;
	}

	public int getPrice() {
		return price;
	}

	public List<String> getUsers() {
		return users;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + price;
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JournalInfo other = (JournalInfo) obj;
		if (price != other.price)
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}

	public static JournalInfo parse(String s) {
		List<String> ss = new ArrayList<>(Arrays.asList(s.split(";")));
		String price = ss.get(0);
		ss.remove(0);
		return new JournalInfo(Integer.parseInt(price), ss);
	}

	public String serialize() {
		return getPrice() + getUsers().stream().reduce("", (s1, s2) -> s1 + ";" + s2);
	}

	public void setPrice(int price) {
		this.price = price;

	}

}
