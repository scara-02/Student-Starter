package com.university;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                false,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {

        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {

        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }
    @Test
    void shouldRejectDuplicateRegistrationWithLowercaseCode() {

        Student student = new Student("S101", "Anita");
        RegistrationService service = new RegistrationService();

        Course originalCourse = new Course(
                "CS101", "Programming", 4, 30, true, null
        );

        Course duplicateCourse = new Course(
                "cs101", "Programming", 4, 30, true, null
        );

        assertTrue(service.register(student, originalCourse));

        int creditsBefore = student.getTotalCredits();
        int enrollmentBefore = duplicateCourse.getCurrentEnrollment();

        boolean result = service.register(student, duplicateCourse);

        assertFalse(result);
        assertEquals(creditsBefore, student.getTotalCredits());
        assertEquals(enrollmentBefore, duplicateCourse.getCurrentEnrollment());
        assertTrue(student.isRegistered("CS101"));
    }

    @Test
    void shouldRejectDuplicateRegistrationWithMixedCaseCode() {

        Student student = new Student("S102", "Rahul");
        RegistrationService service = new RegistrationService();

        Course originalCourse = new Course(
                "CS101", "Programming", 4, 30, true, null
        );

        Course duplicateCourse = new Course(
                "Cs101", "Programming", 4, 30, true, null
        );

        assertTrue(service.register(student, originalCourse));

        int creditsBefore = student.getTotalCredits();
        int enrollmentBefore = duplicateCourse.getCurrentEnrollment();

        boolean result = service.register(student, duplicateCourse);

        assertFalse(result);
        assertEquals(creditsBefore, student.getTotalCredits());
        assertEquals(enrollmentBefore, duplicateCourse.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationForDifferentCourseCode() {

        Student student = new Student("S103", "Priya");
        RegistrationService service = new RegistrationService();

        Course firstCourse = new Course(
                "CS101", "Programming", 4, 30, true, null
        );

        Course differentCourse = new Course(
                "CS102", "Data Structures", 3, 30, true, null
        );

        assertTrue(service.register(student, firstCourse));
        assertTrue(service.register(student, differentCourse));

        assertEquals(7, student.getTotalCredits());
        assertEquals(1, firstCourse.getCurrentEnrollment());
        assertEquals(1, differentCourse.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationWhenOneSeatRemains() {

        Student student = new Student("S104", "Arun");

        Course course = new Course(
                "CS301", "Data Structures", 4, 2, true, null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(
                new Student("S105", "Meena"), course
        ));

        boolean result = service.register(student, course);

        assertTrue(result);
        assertEquals(2, course.getCurrentEnrollment());
        assertEquals(4, student.getTotalCredits());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsFull() {

        Student student = new Student("S106", "Kiran");

        Course course = new Course(
                "CS301", "Data Structures", 4, 1, true, null
        );

        RegistrationService service = new RegistrationService();

        Student existingStudent = new Student("S107", "Meena");
        assertTrue(service.register(existingStudent, course));

        boolean result = service.register(student, course);

        assertFalse(result);
        assertEquals(1, course.getCurrentEnrollment());
        assertEquals(0, student.getTotalCredits());
        assertFalse(student.isRegistered("CS301"));
    }


}