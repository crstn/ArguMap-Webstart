package de.argumap.discussion;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 14.07.2004
 *  
 */
public class Participator {
    private int memberID;
    private String firstName, lastName, eMail;

    /**
     * @param memberID
     * @param firstName
     * @param lastName
     * @param mail
     */
    public Participator(int memberID, String firstName, String lastName,
            String mail) {
        this.memberID = memberID;
        this.firstName = firstName;
        this.lastName = lastName;
        eMail = mail;
    }

    /**
     * @return Returns the eMail.
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * @param mail
     *            The eMail to set.
     */
    public void setEMail(String mail) {
        eMail = mail;
    }

    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return Returns the memberID.
     */
    public int getMemberID() {
        return memberID;
    }

    /**
     * @param memberID
     *            The memberID to set.
     */
    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }
}