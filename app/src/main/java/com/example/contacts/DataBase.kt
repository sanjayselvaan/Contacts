package com.example.contacts

object DataBase {
    private var contactsList = mutableListOf<Contact>()
    fun getContactsList(): List<Contact> = contactsList.sortedBy {
        it.contactName.name.lowercase()
    }

    fun getContactName(position: Int): Name = contactsList[position].contactName

    fun getContactNumber(position: Int): PhoneNumber? = contactsList[position].contactPhoneNumber

    fun getContactEmail(position: Int): Email? = contactsList[position].contactEmail

    fun getContactListSize(): Int = contactsList.size

    fun getContactAddress(position: Int): Address? = contactsList[position].contactAddress

    fun addContact(contact: Contact) {
        contactsList.add(contact)
    }
    init {
        val name = mutableListOf(
            Name("Aaaaa"),
            Name("Bbbbbb"),
            Name("Ccccc"),
            Name("Ddd"),
            Name("Eeee")
        )//,"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
        val phoneNumber = mutableListOf(
            PhoneNumber("1"),
            PhoneNumber("2"),
            PhoneNumber("3"),
            PhoneNumber("4"),
            PhoneNumber("5")
        )//,PhoneNumber(6),PhoneNumber(7),PhoneNumber(8),PhoneNumber(9),PhoneNumber(10),PhoneNumber(11),PhoneNumber(12),PhoneNumber(13),PhoneNumber(14),PhoneNumber(15),PhoneNumber(16),PhoneNumber(17),PhoneNumber(18),PhoneNumber(19),PhoneNumber(20),PhoneNumber(21),PhoneNumber(22),PhoneNumber(23),PhoneNumber(24),PhoneNumber(25),PhoneNumber(26))
        val emailAddress = mutableListOf(
            Email("A@email.com"),
            Email("B@email.com"),
            Email("C@email.com"),
            Email("D@email.com"),
            Email("E@email.com")
        )//,(Email("F@email.com"),(Email("G@email.com"),(Email("H@email.com"),(Email("I@email.com"),(Email("J@email.com"),(Email("K@email.com"),(Email("L@email.com"),(Email("M@email.com"),(Email("P@email.com"),(Email("O@email.com"),(Email("P@email.com"),(Email("Q@email.com"),(Email("R@email.com"),(Email("S@email.com"),(Email("T@email.com"),(Email("U@email.com"),(Email("V@email.com"),(Email("W@email.com"),(Email("X@email.com"),(Email("Y@email.com"),(Email("Z@email.com"))
        val address = mutableListOf(
            Address("A-homeAddress"),
            Address("B-homeAddress"),
            Address("C-homeAddress"),
            Address("D-homeAddress"),
            Address("E-homeAddress")
        )//,Address("-homeAddress"),Address("G-homeAddress"),Address("H-homeAddress"),Address("I-homeAddress"),Address("J-homeAddress"),Address("K-homeAddress"),Address("L-homeAddress"),Address("M-homeAddress"),Address("N-homeAddress"),Address("O-homeAddress"),Address("P-homeAddress"),Address("Q-homeAddress"),Address("R-homeAddress"),Address("S-homeAddress"),Address("T-homeAddress"),Address("U-homeAddress"),Address("V-homeAddress"),Address("W-homeAddress"),Address("X-homeAddress"),Address("Y-homeAddress"),Address("Z-homeAddress"))
        for (i in name.indices) {
            contactsList.add(
                i, Contact(
                    i, name[i],
                    (phoneNumber[i]), emailAddress[i],
                    address[i]
                )
            )
        }
    }

}