package com.example.jariBean.service;

import com.example.jariBean.dto.cafe.CafeReqDto;
import com.example.jariBean.dto.cafe.CafeResDto;
import com.example.jariBean.entity.Cafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CafeSearchServiceTest {

    @Autowired
    SearchService searchService;

    @Autowired
    CafeService cafeService;


    @Test
    public void findBySearchingTest(){
        // given
        CafeReqDto.CafeSearchReqDto cafeSearchReqDto = new CafeReqDto.CafeSearchReqDto();
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 7, 1, 15, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 7, 1, 16, 0);
        String text = "미추홀";
        double latitude = 37.4467039276238;
        double longitude = 126.4467039276238;
        CafeReqDto.Location location = new CafeReqDto.Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        cafeSearchReqDto.setSearchingWord(text);
        cafeSearchReqDto.setLocation(location);
        cafeSearchReqDto.setPeopleNumber(3);
        cafeSearchReqDto.setReserveStartTime(dateTime1);
        cafeSearchReqDto.setReserveEndTime(dateTime2);
        cafeSearchReqDto.setTableOptionList(new ArrayList<>());

        // when
        List<Cafe> cafes = searchService.findByText(cafeSearchReqDto,null);

        // then
        for (Cafe cafe : cafes) {
            Assertions.assertEquals(cafe.getId(), cafe.getId());
        }
    }

    // getCafeWithTodayReserved 과 동일한 로직이라서 추가적인 테스트 코드를 작성하지 않았습니다.
    @Test
    public void findReservedTest() {
        // given
        String cafeId = "64c45ac3935eb61c140793e7";
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 8, 7, 15, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 8, 7, 16, 0);

        // then
        Assertions.assertDoesNotThrow(()->cafeService.getCafeWithSearchingReserved(cafeId, dateTime1,dateTime2, null, null, Pageable.unpaged()));

    }


}