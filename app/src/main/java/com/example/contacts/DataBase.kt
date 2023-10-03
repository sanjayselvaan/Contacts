package com.example.contacts

object DataBase {
    private var contactsList = mutableListOf<Contact>()
    private var idLatest=1000

    fun getContactsList():List<Contact> = contactsList.sortedBy{
        getDisplayName(it)
    }

    fun getContact(id:Int): Contact? {
        contactsList.forEach {
            if (it.contactID==id){
                return it
            }
        }
        return null
    }


    fun addContact(contact: Contact) {
        contactsList.add(contact)
        idLatest++
    }
    fun getLatestAvailableId()= idLatest

    init {
        val name = mutableListOf(
            "Aaaaa",
            "Bbbbbb",
            "Ccccc",
            "Dddd",
            "Eeee"
        )//,"F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
        val phoneNumber = mutableListOf(
            listOf("1"),
            listOf("2"),
            listOf("3"),
            listOf("4"),
            listOf("5")
        )//,PhoneNumber(6),PhoneNumber(7),PhoneNumber(8),PhoneNumber(9),PhoneNumber(10),PhoneNumber(11),PhoneNumber(12),PhoneNumber(13),PhoneNumber(14),PhoneNumber(15),PhoneNumber(16),PhoneNumber(17),PhoneNumber(18),PhoneNumber(19),PhoneNumber(20),PhoneNumber(21),PhoneNumber(22),PhoneNumber(23),PhoneNumber(24),PhoneNumber(25),PhoneNumber(26))
        val emailAddress = mutableListOf(
            listOf("A@email.com"),
            listOf("B@email.com"),
            listOf("C@email.com"),
            listOf("D@email.com"),
            listOf("E@email.com")
        )//,(Email("F@email.com"),(Email("G@email.com"),(Email("H@email.com"),(Email("I@email.com"),(Email("J@email.com"),(Email("K@email.com"),(Email("L@email.com"),(Email("M@email.com"),(Email("P@email.com"),(Email("O@email.com"),(Email("P@email.com"),(Email("Q@email.com"),(Email("R@email.com"),(Email("S@email.com"),(Email("T@email.com"),(Email("U@email.com"),(Email("V@email.com"),(Email("W@email.com"),(Email("X@email.com"),(Email("Y@email.com"),(Email("Z@email.com"))
        val address = mutableListOf(
            listOf("A-homeAddress"),
            listOf("B-homeAddress"),
            listOf("C-homeAddress"),
            listOf("D-homeAddress"),
            listOf("E-homeAddress")
        )//,Address("-homeAddress"),Address("G-homeAddress"),Address("H-homeAddress"),Address("I-homeAddress"),Address("J-homeAddress"),Address("K-homeAddress"),Address("L-homeAddress"),Address("M-homeAddress"),Address("N-homeAddress"),Address("O-homeAddress"),Address("P-homeAddress"),Address("Q-homeAddress"),Address("R-homeAddress"),Address("S-homeAddress"),Address("T-homeAddress"),Address("U-homeAddress"),Address("V-homeAddress"),Address("W-homeAddress"),Address("X-homeAddress"),Address("Y-homeAddress"),Address("Z-homeAddress"))
        for (i in name.indices) {
            contactsList.add(
                i, Contact(
                    idLatest, name[i],
                    (phoneNumber[i]), emailAddress[i],
                    address[i]
                )
            )
            idLatest++
        }
    }
    private fun getDisplayName(contact:Contact): String {
        return when{
            contact.contactName!=null-> contact.contactName
            contact.contactPhoneNumber!=null-> contact.contactPhoneNumber.first()
            contact.contactEmail!=null-> contact.contactEmail.first()
            else->""
        }
    }

}