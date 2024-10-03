package com.studentinfo.views.editprofile;

import com.studentinfo.data.entity.Department;
import com.studentinfo.data.entity.Subject;
import com.studentinfo.data.entity.Teacher;
import com.studentinfo.services.DepartmentService;
import com.studentinfo.services.SubjectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TeacherEditProfileViewTest {

    private TeacherEditProfileView teacherEditProfileView;

    @BeforeEach
    public void setUp() {
        // Mock the Teacher, DepartmentService, and SubjectService
        Teacher teacher = Mockito.mock(Teacher.class);
        DepartmentService departmentService = Mockito.mock(DepartmentService.class);
        SubjectService subjectService = Mockito.mock(SubjectService.class);

        // Mock methods for departmentService and subjectService
        when(departmentService.findAll()).thenReturn(Collections.emptyList());
        when(subjectService.findAll()).thenReturn(Collections.emptyList());

        // Initialize a Vaadin UI context
        UI ui = new UI();
        UI.setCurrent(ui);

        // Instantiate the view
        System.out.println("Setting up TeacherEditProfileView...");
        teacherEditProfileView = new TeacherEditProfileView(teacher, departmentService, subjectService);
        System.out.println("TeacherEditProfileView setup completed.");
    }

    @Test
    public void testTeacherEditProfileViewComponents() throws Exception {
        System.out.println("Testing TeacherEditProfileView components...");

        // Verify that the main layout is not null
        assertNotNull(teacherEditProfileView, "The TeacherEditProfileView should not be null.");

        // Access private fields using reflection
        Field firstNameField = TeacherEditProfileView.class.getDeclaredField("firstNameField");
        firstNameField.setAccessible(true);
        TextField firstNameTextField = (TextField) firstNameField.get(teacherEditProfileView);
        assertNotNull(firstNameTextField, "First Name field should be present in the view.");
        System.out.println("First Name field is present.");

        Field departmentComboBoxField = TeacherEditProfileView.class.getDeclaredField("departmentComboBox");
        departmentComboBoxField.setAccessible(true);
        ComboBox<Department> departmentComboBox = (ComboBox<Department>) departmentComboBoxField.get(teacherEditProfileView);
        assertNotNull(departmentComboBox, "Department ComboBox should be present in the view.");
        System.out.println("Department ComboBox is present.");

        Field saveButtonField = TeacherEditProfileView.class.getDeclaredField("saveButton");
        saveButtonField.setAccessible(true);
        Button saveButton = (Button) saveButtonField.get(teacherEditProfileView);
        assertNotNull(saveButton, "Save button should be present in the view.");
        System.out.println("Save button is present.");

        // Simulate clicking the save button
        System.out.println("Clicking the save button...");
        assertDoesNotThrow(saveButton::click, "Clicking the save button should not throw an exception.");
        System.out.println("Save button clicked successfully.");
    }
}
