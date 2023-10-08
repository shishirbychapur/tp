package seedu.lovebook.logic.parser;

import static seedu.lovebook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.lovebook.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.lovebook.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.AGE_DESC_AMY;
import static seedu.lovebook.logic.commands.CommandTestUtil.AGE_DESC_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.GENDER_DESC_AMY;
import static seedu.lovebook.logic.commands.CommandTestUtil.GENDER_DESC_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.lovebook.logic.commands.CommandTestUtil.INVALID_AGE_DESC;
import static seedu.lovebook.logic.commands.CommandTestUtil.INVALID_GENDER_DESC;
import static seedu.lovebook.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.lovebook.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.lovebook.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.lovebook.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.lovebook.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.lovebook.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.lovebook.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_AGE_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_GENDER_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.lovebook.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.lovebook.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.lovebook.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.lovebook.testutil.TypicalPersons.AMY;
import static seedu.lovebook.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.lovebook.logic.Messages;
import seedu.lovebook.logic.commands.AddCommand;
import seedu.lovebook.model.person.Height;
import seedu.lovebook.model.person.Age;
import seedu.lovebook.model.person.Date;
import seedu.lovebook.model.person.Gender;
import seedu.lovebook.model.person.Name;
import seedu.lovebook.model.tag.Tag;
import seedu.lovebook.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Date expectedDate = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedDate));


        // multiple tags - all accepted
        Date expectedDateMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedDateMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple ages
        assertParseFailure(parser, AGE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AGE));

        // multiple genders
        assertParseFailure(parser, GENDER_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GENDER));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + AGE_DESC_AMY + GENDER_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_GENDER, PREFIX_AGE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid gender
        assertParseFailure(parser, INVALID_GENDER_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GENDER));

        // invalid age
        assertParseFailure(parser, INVALID_AGE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AGE));

        // invalid lovebook
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid gender
        assertParseFailure(parser, validExpectedPersonString + INVALID_GENDER_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GENDER));

        // invalid age
        assertParseFailure(parser, validExpectedPersonString + INVALID_AGE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AGE));

        // invalid lovebook
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Date expectedDate = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + AGE_DESC_AMY + GENDER_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedDate));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + AGE_DESC_BOB + GENDER_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing age prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_AGE_BOB + GENDER_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing gender prefix
        assertParseFailure(parser, NAME_DESC_BOB + AGE_DESC_BOB + VALID_GENDER_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing lovebook prefix
        assertParseFailure(parser, NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB + VALID_ADDRESS_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_AGE_BOB + VALID_GENDER_BOB + VALID_ADDRESS_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + AGE_DESC_BOB + GENDER_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid age
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_AGE_DESC + GENDER_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Age.MESSAGE_CONSTRAINTS);

        // invalid gender
        assertParseFailure(parser, NAME_DESC_BOB + AGE_DESC_BOB + INVALID_GENDER_DESC + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Gender.MESSAGE_CONSTRAINTS);

        // invalid lovebook
        assertParseFailure(parser, NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB + INVALID_ADDRESS_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Height.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + AGE_DESC_BOB + GENDER_DESC_BOB + INVALID_ADDRESS_DESC,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + AGE_DESC_BOB + GENDER_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
