package ai.dragon.dto.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import ai.dragon.repository.util.Pager;

@ActiveProfiles("test")
public class DataTableApiResponseTest {
    @Test
    void testFromPagerWithData() {
        // Arrange
        Pager<String> pager = Pager.<String>builder()
                .page(1)
                .size(10)
                .total(100)
                .data(Arrays.asList("item1", "item2", "item3"))
                .build();

        // Act
        DataTableApiResponse response = DataTableApiResponse.fromPager(pager);

        // Assert
        assertNotNull(response);
        assertEquals("0000", response.getCode());
        assertEquals("OK", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(3, response.getData().getRecords().size());
        assertEquals(1, response.getData().getCurrent());
        assertEquals(10, response.getData().getSize());
        assertEquals(100, response.getData().getTotal());
    }

    @Test
    void testFromPagerWithNoData() {
        // Arrange
        Pager<String> pager = Pager.<String>builder()
                .page(1)
                .size(10)
                .total(0)
                .data(Collections.emptyList())
                .build();

        // Act
        DataTableApiResponse response = DataTableApiResponse.fromPager(pager);

        // Assert
        assertNotNull(response);
        assertEquals("0000", response.getCode());
        assertEquals("OK", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getRecords().size());
        assertEquals(1, response.getData().getCurrent());
        assertEquals(10, response.getData().getSize());
        assertEquals(0, response.getData().getTotal());
    }

    @Test
    void testFromPagerWithEdgeCases() {
        // Arrange
        Pager<String> pager = Pager.<String>builder()
                .page(0)
                .size(0)
                .total(0)
                .data(Collections.emptyList())
                .build();

        // Act
        DataTableApiResponse response = DataTableApiResponse.fromPager(pager);

        // Assert
        assertNotNull(response);
        assertEquals("0000", response.getCode());
        assertEquals("OK", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getRecords().size());
        assertEquals(0, response.getData().getCurrent());
        assertEquals(0, response.getData().getSize());
        assertEquals(0, response.getData().getTotal());
    }
}
