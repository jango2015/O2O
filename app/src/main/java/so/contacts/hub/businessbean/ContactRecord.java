package so.contacts.hub.businessbean;

public abstract class ContactRecord implements Comparable<ContactRecord> {

	protected abstract String getDate();

	@Override
	public int compareTo(ContactRecord another) {
		long aData = Long.parseLong(getDate());
		long anotherDate = Long.parseLong(another.getDate());
		return aData == anotherDate ? 0 : (aData < anotherDate ? 1 : -1);
	}

}
