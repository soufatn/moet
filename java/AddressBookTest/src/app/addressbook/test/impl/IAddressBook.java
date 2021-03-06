package app.addressbook.test.impl;

import app.addressbook.test.Contact;

/**
 * Application APIs to test.
 * @author eong
 *
 */
public interface IAddressBook 
{	
	/**
	 * Add contact to address book app.
	 * @param contact Contact info object
	 * @return true if success
	 * @throws Exception exception
	 */
	public boolean addContact(Contact contact) throws Exception;
	
	/**
	 * Find contact to address book app.
	 * @param contact Contact info object
	 * @return true if success
	 * @throws Exception exception
	 */
	public boolean findContact(Contact contact) throws Exception;
	
	/**
	 * Delete contact to address book app.
	 * @param contact Contact info object
	 * @return true if success
	 * @throws Exception exception
	 */
	public boolean deleteContact(Contact contact) throws Exception;
	
}
