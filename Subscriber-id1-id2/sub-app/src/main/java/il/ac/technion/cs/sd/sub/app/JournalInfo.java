package il.ac.technion.cs.sd.sub.app;

import java.util.Arrays;
import java.util.List;

public class JournalInfo {
	private final int price;
	private final List<String> users;

	public JournalInfo(int price, List<String> users) {
		this.price = price;
		this.users = users;
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
	
	public static JournalInfo parse(String s){
		List<String> ss = Arrays.asList(s.split(";"));
		String price = ss.get(0);
		ss.remove(0);
		return new JournalInfo(Integer.parseInt(price), ss);
	}
	
	public String serialize() {
		return getPrice() + getUsers().stream().reduce("", (s1,s2)->s1 + ";" + s2); 
	}

}
