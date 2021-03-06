package uk.co.compendiumdev.thingifier.domain.definitions.validation;

public class VRule {

    public static ValidationRule notEmpty() {
        return new NotEmptyValidationRule();
    }

    public static ValidationRule maximumLength(final int maxLength) {
        return new MaximumLengthValidationRule(maxLength);
    }

    public static ValidationRule matchesRegex(final String regexToMatch) {
        return new MatchesRegexValidationRule(regexToMatch);
    }
    public static ValidationRule satisfiesRegex(final String regexToFind) {
        return new FindsRegexValidationRule(regexToFind);
    }
}
