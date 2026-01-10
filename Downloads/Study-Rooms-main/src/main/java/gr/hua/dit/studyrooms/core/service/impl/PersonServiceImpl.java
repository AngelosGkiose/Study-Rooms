package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.service.PersonService;
import gr.hua.dit.studyrooms.core.service.mapper.PersonMapper;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonResult;
import gr.hua.dit.studyrooms.core.service.model.PersonView;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Default implementation of {@link PersonService}.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonServiceImpl(final PasswordEncoder passwordEncoder,final PersonRepository personRepository,
                             final PersonMapper personMapper) {
        if (passwordEncoder == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();

        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;

    }

    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest,final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException();

        // Unpack (we assume valid `CreatePersonRequest` instance)
        // --------------------------------------------------

        final PersonType type = createPersonRequest.type();
        final String identifier = createPersonRequest.identifier().strip(); // remove whitespaces
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();

        // Basic email address validation.
        // --------------------------------------------------

        if (!emailAddress.endsWith("@hua.gr")) {
            return CreatePersonResult.fail("Only academic email addresses (@hua.gr) are allowed");
        }

        // Advanced mobile phone number validation.
        // --------------------------------------------------


        // --------------------------------------------------

        if (this.personRepository.existsByIdentifierIgnoreCase(identifier)) {
            return CreatePersonResult.fail("HUA ID already registered");
        }

        if (this.personRepository.existsByEmailAddressIgnoreCase(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // --------------------------------------------------

        // --------------------------------------------------

        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        // Instantiate person.
        // --------------------------------------------------

        Person person = new Person();
        person.setId(null); // auto generated
        person.setIdentifier(identifier);
        person.setType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(emailAddress);
        person.setMobilePhoneNumber(mobilePhoneNumber);
        person.setPasswordHash(hashedPassword);
        person.setCreatedAt(null); // auto generated.

        // Persist person (save/insert to database)
        // --------------------------------------------------

        person = this.personRepository.save(person);

        // --------------------------------------------------


        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // --------------------------------------------------

        return CreatePersonResult.success(personView);
    }
}