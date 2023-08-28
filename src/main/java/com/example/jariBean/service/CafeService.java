package com.example.jariBean.service;

import com.example.jariBean.dto.cafe.CafeResDto.CafeDetailDto;
import com.example.jariBean.dto.cafe.CafeResDto.CafeDetailReserveDto;
import com.example.jariBean.dto.cafe.CafeResDto.CafeSummaryDto;
import com.example.jariBean.dto.reserved.ReservedResDto.TableReserveResDto;
import com.example.jariBean.dto.reserved.ReservedResDto.availableTime;
import com.example.jariBean.dto.table.TableResDto.TableDetailDto;
import com.example.jariBean.entity.Cafe;
import com.example.jariBean.entity.CafeOperatingTime;
import com.example.jariBean.entity.Reserved;
import com.example.jariBean.entity.TableClass;
import com.example.jariBean.handler.ex.CustomDBException;
import com.example.jariBean.repository.cafe.CafeRepository;
import com.example.jariBean.repository.cafeOperatingTime.CafeOperatingTimeRepository;
import com.example.jariBean.repository.matching.MatchingRepository;
import com.example.jariBean.repository.reserved.ReservedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeService {
    private final CafeRepository cafeRepository;

    private final MatchingRepository matchingRepository;

    private final ReservedRepository reservedRepository;



    public List<CafeSummaryDto> getCafeByMatchingCount(Pageable pageable){
        try {
            List<String> cafeList = matchingRepository.findCafeIdSortedByCount(pageable);
            List<CafeSummaryDto> cafeSummaryDtos = new ArrayList<>();
            cafeRepository.findByIds(cafeList).forEach(cafe -> cafeSummaryDtos.add(new CafeSummaryDto(cafe)));
            return cafeSummaryDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomDBException("DB에 조회하신 정보가 없습니다.");
        }

    }

    public CafeDetailReserveDto getCafeWithSearchingReserved(String cafeId, LocalDateTime reserveStartTime, LocalDateTime reserveEndTime, Integer peopleNumber, List<TableClass.TableOption> tableOptions,
                                                             Pageable pageable) {

        CafeDetailReserveDto cafeDetailReserveDto = new CafeDetailReserveDto();
        try {
            Cafe cafe = cafeRepository.findById(cafeId).orElseThrow();
            cafeDetailReserveDto.setCafeDetailDto(new CafeDetailDto(cafe));

            Map<String, List<Reserved>> reservedListByTabldIdWithPagination = new LinkedHashMap<>();
            reservedRepository.findReservedByConditions(cafeId, reserveStartTime, reserveEndTime, peopleNumber, tableOptions).forEach(reserved ->
                    {
                        if (!reservedListByTabldIdWithPagination.containsKey(reserved.getTable().getId())){
                            reservedListByTabldIdWithPagination.put(reserved.getTable().getId(), new ArrayList<>());
                        }
                        reservedListByTabldIdWithPagination.get(reserved.getTable().getId()).add(reserved);
                    }
            );

            List<Map.Entry<String, List<Reserved>>> reservedListByTabldId = reservedListByTabldIdWithPagination
                    .entrySet()
                    .stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .toList();

            for (Map.Entry<String, List<Reserved>> reservedList :reservedListByTabldId){
                TableReserveResDto tableReserveResDto = new TableReserveResDto();
                TableDetailDto tableDetailDto = new TableDetailDto(reservedList.getValue().get(0).getTable());
                tableReserveResDto.setTableDetailDto(tableDetailDto);

                List<availableTime> times = new ArrayList<>();
                LocalDateTime startTime = reserveStartTime;
                LocalDateTime endTime = reserveEndTime;
                for (Reserved reserved : reservedList.getValue()) {
                    if (!startTime.isEqual(reserved.getStartTime())){
                        times.add(new availableTime(startTime, reserved.getStartTime()));
                    }
                    startTime = reserved.getEndTime();
                }
                if (!startTime.isEqual(endTime)){
                    times.add(new availableTime(startTime, endTime));
                }

                tableReserveResDto.setAvailableTimeList(times);
                cafeDetailReserveDto.addTable(tableReserveResDto);
            }
            return cafeDetailReserveDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomDBException("해당 되는 카페가 없습니다.");
        }

    }


    public CafeDetailReserveDto getCafeWithTodayReserved(String cafeId, Pageable pageable){

        CafeDetailReserveDto cafeDetailReserveDto = new CafeDetailReserveDto();
        LocalDateTime now = LocalDateTime.now();
        try {
            Cafe cafe = cafeRepository.findById(cafeId).orElseThrow();
            cafeDetailReserveDto.setCafeDetailDto(new CafeDetailDto(cafe));


            Map<String, List<Reserved>> reservedListByTabldIdWithPagination = new LinkedHashMap<>();
            reservedRepository.findTodayReservedById(cafeId).forEach(reserved ->
                    {
                        if (!reservedListByTabldIdWithPagination.containsKey(reserved.getTable().getId())){
                            reservedListByTabldIdWithPagination.put(reserved.getTable().getId(), new ArrayList<>());
                        }
                        reservedListByTabldIdWithPagination.get(reserved.getTable().getId()).add(reserved);
                    }
            );

            List<Map.Entry<String, List<Reserved>>> reservedListByTabldId = reservedListByTabldIdWithPagination
                    .entrySet()
                    .stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .toList();

            for (Map.Entry<String, List<Reserved>> reservedList :reservedListByTabldId){

                TableReserveResDto tableReserveResDto = new TableReserveResDto();
                TableDetailDto tableDetailDto = new TableDetailDto(reservedList.getValue().get(0).getTable());
                tableReserveResDto.setTableDetailDto(tableDetailDto);

                List<availableTime> times = new ArrayList<>();
                LocalDateTime startTime = cafe.getStartTime().withDayOfMonth(now.getDayOfMonth()).withMonth(now.getMonthValue()).withYear(now.getYear());
                LocalDateTime endTime = cafe.getEndTime().withDayOfMonth(now.getDayOfMonth()).withMonth(now.getMonthValue()).withYear(now.getYear());
                for (Reserved reserved : reservedList.getValue()) {
                    if (!startTime.isEqual(reserved.getStartTime())){
                        times.add(new availableTime(startTime, reserved.getStartTime()));
                    }
                    startTime = reserved.getEndTime();
                }
                if (!startTime.isEqual(endTime)){
                    times.add(new availableTime(startTime, endTime));
                }

                tableReserveResDto.setAvailableTimeList(times);
                cafeDetailReserveDto.addTable(tableReserveResDto);
            }
            return cafeDetailReserveDto;
        } catch (Exception e) {
            throw new CustomDBException("해당 되는 카페가 없습니다.");
        }

    }

}
