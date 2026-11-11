package se.yrgo.libraryapp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.yrgo.libraryapp.dao.ClassificationDao;
import se.yrgo.libraryapp.entities.DdsClassification;

@ExtendWith(MockitoExtension.class)
public class ClassificationControllerTest {

    @Mock
    ClassificationDao classificationDao;

    ClassificationController classificationController;

    @BeforeEach
    void setUp() {
        classificationController = new ClassificationController(classificationDao);
    }

    @Test
    void getAvailableClassifications() {
        List<DdsClassification> expected = List.of(
                new DdsClassification(0, "Computer programming, programs and data"),
                new DdsClassification(10, "Bibliography"));

        when(classificationDao.getAvailable()).thenReturn(expected);

        List<DdsClassification> result = classificationController.getAvailable();

        assertThat(result).containsExactlyElementsOf(expected);

        verify(classificationDao).getAvailable();
    }

    @Test
    void getAvailableClassificationsReturnsEmptyList() {
        when(classificationDao.getAvailable()).thenReturn(List.of());

        List<DdsClassification> result = classificationController.getAvailable();

        assertThat(result).isEmpty();

        verify(classificationDao).getAvailable();
    }

}
