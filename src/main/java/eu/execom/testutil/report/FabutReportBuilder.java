package eu.execom.testutil.report;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import eu.execom.testutil.enums.CommentType;
import eu.execom.testutil.pair.AssertPair;
import eu.execom.testutil.property.NotNullProperty;
import eu.execom.testutil.property.NullProperty;

/**
 * Class which purpose is to build pretty info messages about asserting.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
// TODO DUNNO add code snipets how error message look like and with description.

// TODO all method names should be reviewed and renamed to make more sense of what they are reporting
public class FabutReportBuilder {

    private static final String ARROW = ">";
    private static final String NEW_LINE = "\n";
    private static final String TAB = "    ";
    private static final String EMPTY_STRING = "";

    private static final String NOT_NULL_PROPERTY_SUCCESS = "%s: expected not null property and was not null";
    private static final String NOT_NULL_PROPERTY_FAIL = "%s: expected not null property, but was null";
    private static final String NULL_PROPERTY_FAIL = "%s: expected null property, but was not null";
    private static final String NULL_PROPERTY_SUCCESS = "%s: expected null property and was null";
    private static final String IGNORE_PROPERTY = "%s: is ignore property";
    private static final String PROPERTY_WRONG_REFERENCE = "Property:  %s of class:  %s has wrong reference.";
    private static final String PROPERTY_GOOD_REFERENCE = "Property:  %s of class:  %s has good reference.";
    private static final String ASSERT_LIST = "Assert object at index  %s of list  %s.";
    private static final String NO_PROPERTY_FOR_FIELD = "There was no property for field:  %s of class:  %s, with value: %s";
    private static final String IGNORED_TYPE = "Type  %s is ignored type.";
    private static final String LIST_SIZE = "list size ";
    private static final String UNINVOKABLE_METHOD = "There is no method: %s in actual class: %s (expected class was: %s).";
    private static final String REPOSITORY_FAILURE_NO_ENTITY_IN_DB = "-> Entity %s doesn't exist in DB any more "
            + "but is not asserted in test.";
    private static final String REPOSITORY_FAILURE_ENTITY_ISNT_ASSERTED = "-> Entity %s is created in system after "
            + "last snapshot but hasnt been asserted in test.";
    private static final String REPOSITORY_ENTITY_ASSERT_FAIL = "-> Expected entity: <<%s>> but was: <<%s>>, of %s.";
    private static final String NULL_REFERENCE_ASSERT = "Trying to assert null object reference.";
    private static final String PARAMETERS_ASSERT_FAIL = "-> Expected parameter: <<%s>> but was: <<%s>>, of %s.";
    private static final String NO_VALID_COPY = "There is no public default constructor, or there is no matching public set method for field which have corresponding get method, in class: %s.";

    private final StringBuilder builder;
    private Integer assertDepth;
    private final List<String> messageParts;
    private int failedMessagePosition;

    /**
     * Default constructor.
     */
    public FabutReportBuilder() {
        builder = new StringBuilder();
        assertDepth = 0;
        messageParts = new ArrayList<String>();
        failedMessagePosition = 0;
    }

    /**
     * Default constructor.
     * 
     * @param message
     *            initial message
     */
    public FabutReportBuilder(final String message) {
        this();
        messageParts.add(message.length() > 0 ? "\n" + message : message);
        failedMessagePosition = 1;

    }

    /**
     * Get string text.
     * 
     * @return string
     */
    public String getMessage() {

        for (final String part : messageParts) {
            builder.append(part);
        }
        return builder.toString();
    }

    /**
     * Add new comment to specified depth.
     * 
     * @param propertyName
     *            name of property
     * @param comment
     *            additional comment
     * @param expected
     *            value
     * @param actual
     *            value
     * @param type
     *            type of comment
     * 
     * @param generic
     *            type
     */
    public void addComment(final String propertyName, final String comment, final Object expected, final Object actual,
            final CommentType type) {
        switch (type) {
        case FAIL:
            addComment(propertyName, comment + " expected: " + expected + " but was: " + actual, type);
            break;
        case SUCCESS:
            addComment(propertyName, comment + " expected: " + expected + " and was: " + actual, type);
            break;
        default:
            throw new IllegalStateException("Unsupported CommentyType: " + type);
        }
    }

    public void addComment(final String propertyName, final Object expected, final Object actual, final CommentType type) {
        addComment(propertyName, EMPTY_STRING, expected, actual, type);
    }

    /**
     * Add new comment to specified depth.
     * 
     * @param propertyName
     *            name of property
     * @param comment
     *            to be added
     * @param type
     *            type of comment
     */
    public void addComment(final String propertyName, final String comment, final CommentType type) {

        final StringBuilder part = new StringBuilder(builder.toString());
        builder.setLength(0);
        part.append(NEW_LINE);
        for (int i = 0; i <= assertDepth; i++) {
            if (i == assertDepth) {
                part.append(type.getMark());
                part.append(ARROW);
            } else {
                part.append(TAB);
            }
        }

        part.append(propertyName);
        part.append(": ");
        part.append(comment);
        if (type == CommentType.FAIL) {
            messageParts.add(failedMessagePosition, part.toString());
        } else {
            messageParts.add(part.toString());
        }

    }

    /**
     * Reports fail due to different types.
     * 
     * @param propertyName
     *            name of property
     * @param comment
     *            to be added
     * @param expected
     *            value
     * @param actual
     *            value
     * @param generic
     *            type
     */
    // TODO 1st check if this method is still needed, then refactor it
    public void addDifferentTypeComment(final String propertyName, final String comment, final Object expected,
            final Object actual) {
        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));
        part.append("type expected: " + expected.getClass().getSimpleName() + " but was: "
                + actual.getClass().getSimpleName());
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Reports fail due to different list sizes.
     * 
     * @param propertyName
     *            name of list property
     * @param expectedSize
     *            - expected list's size
     * @param actualSize
     *            - actual list's size
     */
    public void addListDifferentSizeComment(final String propertyName, final int expectedSize, final int actualSize) {

        addComment(propertyName, LIST_SIZE, expectedSize, actualSize, CommentType.FAIL);
    }

    /**
     * Reports fail due to field not having matching property.
     * 
     * @param fieldName
     *            - name of the field
     * @param field
     *            - class of the field
     */
    public void addNoPropertyForFieldComment(final String fieldName, final Method method, final Object actual) {

        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));
        Object field = null;
        try {
            field = method.invoke(actual);
        } catch (final Exception e) {
            field = EMPTY_STRING;
        }
        part.append(String.format(NO_PROPERTY_FOR_FIELD, fieldName, method.getReturnType().getSimpleName(),
                field == null ? null : field.toString()));
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Reports result of assertion with {@link NotNullProperty}.
     * 
     * @param fieldName
     *            - name of the field
     * @param assertResult
     *            - assert result
     */
    public void reportNotNullProperty(final String fieldName, final boolean assertResult) {

        final StringBuilder part = new StringBuilder(EMPTY_STRING);
        if (assertResult) {
            part.append(indentNewLine(CommentType.SUCCESS));
            part.append(String.format(NOT_NULL_PROPERTY_SUCCESS, fieldName));
            messageParts.add(part.toString());
        } else {
            part.append(indentNewLine(CommentType.FAIL));
            part.append(String.format(NOT_NULL_PROPERTY_FAIL, fieldName));
            messageParts.add(failedMessagePosition, part.toString());
        }
    }

    /**
     * Reports result of of assertion with {@link NullProperty}.
     * 
     * @param fieldName
     *            - name of the field
     * @param assertResult
     *            - assert result
     */
    public void reportNullProperty(final String fieldName, final boolean assertResult) {

        final StringBuilder part = new StringBuilder(EMPTY_STRING);
        if (assertResult) {
            part.append(indentNewLine(CommentType.SUCCESS));
            part.append(String.format(NULL_PROPERTY_SUCCESS, fieldName));
            messageParts.add(part.toString());
        } else {
            part.append(indentNewLine(CommentType.FAIL));
            part.append(String.format(NULL_PROPERTY_FAIL, fieldName));
            messageParts.add(failedMessagePosition, part.toString());
        }
    }

    /**
     * Reports ignore property.
     * 
     * @param fieldName
     *            - name of the field
     */
    public void reportIgnoreProperty(final String fieldName) {

        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.SUCCESS));
        part.append(String.format(IGNORE_PROPERTY, fieldName));
        messageParts.add(part.toString());
    }

    /**
     * Report result of reference assert.
     * 
     * @param fieldName
     *            - name of the field
     * @param fieldClass
     *            - field class
     * @param assertResult
     *            - assert result
     */
    public void reportPointsTo(final String fieldName, final Object object, final boolean assertResult) {

        final Class<?> fieldClass = object.getClass();
        final StringBuilder part = new StringBuilder(EMPTY_STRING);

        if (assertResult) {
            part.append(indentNewLine(CommentType.SUCCESS));
            part.append(String.format(PROPERTY_GOOD_REFERENCE, fieldName, fieldClass.getSimpleName()));
            messageParts.add(part.toString());
        } else {
            part.append(indentNewLine(CommentType.FAIL));
            part.append(String.format(PROPERTY_WRONG_REFERENCE, fieldName, fieldClass.getSimpleName()));
            messageParts.add(failedMessagePosition, part.toString());
        }
    }

    /**
     * Reports ignore type.
     * 
     * @param expected
     *            object
     * @param actual
     *            object
     * 
     * @param generic
     *            type
     */
    public void reportIgnoredType(final Object expected, final Object actual) {
        String fieldName;
        if (expected != null) {
            fieldName = expected.getClass().getSimpleName();
        } else {
            fieldName = actual.getClass().getSimpleName();
        }

        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.SUCCESS));
        part.append(String.format(IGNORED_TYPE, fieldName));
        messageParts.add(part.toString());
    }

    public void reportIgnoredType(final AssertPair assertPair) {
        reportIgnoredType(assertPair.getExpected(), assertPair.getActual());
    }

    /**
     * Reports asserting list elements by index.
     * 
     * @param listName
     *            - name of the list
     * @param index
     *            - index of the element in the list beeing asserted
     */
    public void reportAssertingListElement(final String listName, final int index) {
        builder.append(indentNewLine(CommentType.LIST));
        builder.append(String.format(ASSERT_LIST, index, listName));
    }

    /**
     * Reports entity assert.
     * 
     * @param expectedId
     *            - Id of expected entity
     * @param actualId
     *            - Id of actual entity
     * @param assertResult
     *            - assert result
     * 
     * @param <ID>
     *            type of entity identification object
     */
    public <ID> void reportEntityAssert(final ID expectedId, final ID actualId, final boolean assertResult) {
        increaseDepth();
        final String stringExpectedId = expectedId != null ? expectedId.toString() : null;
        final String stringActualId = actualId != null ? actualId.toString() : null;

        addComment(EMPTY_STRING, EMPTY_STRING, stringExpectedId, stringActualId, assertResult ? CommentType.SUCCESS
                : CommentType.FAIL);
        decreaseDepth();
    }

    /**
     * Reports failure when there is no matching entity in current snapshot for entity in before snapshot.
     * 
     * @param propertyName
     *            - property name
     */
    public void reportNoEntityFailure(final Object entity) {
        final StringBuilder part = new StringBuilder(NEW_LINE + NEW_LINE);
        part.append(String.format(REPOSITORY_FAILURE_NO_ENTITY_IN_DB, entity.toString()));
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Reports failure when entity in current snapshot doesn't have matching entity in before snapshot and isn't
     * asserted in test.
     * 
     * 
     * @param entities
     *            list of non asserted entities
     * 
     * @param generic
     *            type
     */
    public void reportEntityIsntAsserted(final Object entity) {
        final StringBuilder part = new StringBuilder(EMPTY_STRING);
        part.append(NEW_LINE + NEW_LINE);
        part.append(String.format(REPOSITORY_FAILURE_ENTITY_ISNT_ASSERTED, entity.toString()));
        messageParts.add(part.toString());
    }

    /**
     * Report repository entity assert fail.
     * 
     * @param the
     *            generic type
     * @param beforeEntity
     *            the before entity
     * @param afterEntity
     *            the after entity
     */
    public void reportRepositoryEntityAssertFail(final Object beforeEntity, final Object afterEntity) {
        final StringBuilder part = new StringBuilder();
        part.append(NEW_LINE);
        part.append(String.format(REPOSITORY_ENTITY_ASSERT_FAIL, beforeEntity.toString(), afterEntity.toString(),
                afterEntity.getClass()));
        messageParts.add(part.toString());
    }

    /**
     * Reports uninvokable method.
     * 
     * @param methodName
     *            - name of the method
     */
    public void reportUninvokableMethod(final Method method, final Object actual) {
        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));
        part.append(String.format(UNINVOKABLE_METHOD, method.getName(), actual.getClass().getSimpleName(), method
                .getDeclaringClass().getSimpleName()));
        messageParts.add(failedMessagePosition, part.toString());
    }

    public void reportUninvokableMethod(final Method method, final AssertPair assertPair) {
        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));
        part.append(String.format(UNINVOKABLE_METHOD, method.getName(), assertPair.getActual().getClass()
                .getSimpleName(), assertPair.getExpected().getClass().getSimpleName()));
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Reports fail due to passing null object reference for asserting.
     * 
     * @param actual
     *            object reference for asserting
     */
    public void addNullReferenceAssertComment() {

        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));

        part.append(String.format(NULL_REFERENCE_ASSERT));
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Indents new line to assert depth with {@link CommentType}.
     * 
     * @param type
     *            - type of comment
     */
    protected String indentNewLine(final CommentType type) {

        final StringBuilder part = new StringBuilder(NEW_LINE);
        for (int i = 0; i <= assertDepth; i++) {
            if (i == assertDepth) {
                part.append(type.getMark());
                part.append(ARROW);
            } else {
                part.append(TAB);
            }
        }
        return part.toString();
    }

    /**
     * Report parameters assert fail.
     * 
     * @param beforeParameter
     *            the before parameter
     * @param afterParameter
     *            the after parameter
     */
    public void reportParametersAssertFail(final Object beforeParameter, final Object afterParameter) {
        final StringBuilder part = new StringBuilder();
        part.append(NEW_LINE);
        part.append(String.format(PARAMETERS_ASSERT_FAIL, beforeParameter.toString(), afterParameter.toString(),
                afterParameter.getClass()));
        messageParts.add(part.toString());
    }

    /**
     * Reports fail due to passing null object reference for asserting.
     * 
     * @param original
     *            parameter for copying
     */
    public void reportNoValidCopy(final Object original) {

        final StringBuilder part = new StringBuilder(indentNewLine(CommentType.FAIL));

        part.append(String.format(NO_VALID_COPY, original == null ? EMPTY_STRING : original.getClass().getSimpleName()));
        messageParts.add(failedMessagePosition, part.toString());
    }

    /**
     * Increase depth.
     */
    public void increaseDepth() {

        assertDepth++;
    }

    /**
     * Decrease depth.
     */
    public void decreaseDepth() {

        assertDepth--;
    }

    /**
     * Appends message.
     * 
     * @param message
     *            message for appending
     */
    public void append(final String message) {
        messageParts.add(message);
    }

}