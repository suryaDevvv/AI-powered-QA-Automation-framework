package tests.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record TestUser(
        String name, String email, String password, String title, String birthDate, String birthMonth,
        String birthYear, String firstName, String lastName, String company, String address1, String address2,
        String country, String zipCode, String state, String city, String mobileNumber) {

    public static TestUser fresh() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return new TestUser("QA User", "qa." + suffix + "@example.com", TestConfig.testPassword(),
                "Mrs", "12", "May", "1992", "Qa", "Tester", "QA Practice", "21 Test Street",
                "Suite 2", "India", "560001", "Karnataka", "Bengaluru", "9876543210");
    }

    public TestUser withName(String changedName) {
        return new TestUser(changedName, email, password, title, birthDate, birthMonth, birthYear,
                firstName, lastName, company, address1, address2, country, zipCode, state, city, mobileNumber);
    }

    public TestUser withPassword(String changedPassword) {
        return new TestUser(name, email, changedPassword, title, birthDate, birthMonth, birthYear,
                firstName, lastName, company, address1, address2, country, zipCode, state, city, mobileNumber);
    }

    public Map<String, Object> toFormParams() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("title", title);
        params.put("birth_date", birthDate);
        params.put("birth_month", birthMonth);
        params.put("birth_year", birthYear);
        params.put("firstname", firstName);
        params.put("lastname", lastName);
        params.put("company", company);
        params.put("address1", address1);
        params.put("address2", address2);
        params.put("country", country);
        params.put("zipcode", zipCode);
        params.put("state", state);
        params.put("city", city);
        params.put("mobile_number", mobileNumber);
        return params;
    }
}
