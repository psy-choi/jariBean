package com.example.jariBean.service;

import com.example.jariBean.dto.manager.ManagerReqDto.ManagerJoinReqDto;
import com.example.jariBean.dto.manager.ManagerReqDto.ManagerTableClassReqDto;
import com.example.jariBean.dto.manager.ManagerResDto.ManagerLoginResDto;
import com.example.jariBean.dto.manager.ManagerResDto.ReserveDto;
import com.example.jariBean.dto.manager.ManagerResDto.TableClassDto;
import com.example.jariBean.dto.manager.ManagerResDto.TableDto;
import com.example.jariBean.entity.CafeManager;
import com.example.jariBean.entity.Table;
import com.example.jariBean.entity.TableClass;
import com.example.jariBean.handler.ex.CustomDBException;
import com.example.jariBean.repository.cafe.CafeRepository;
import com.example.jariBean.repository.cafemanager.CafeManagerRepository;
import com.example.jariBean.repository.reserved.ReservedRepository;
import com.example.jariBean.repository.table.TableRepository;
import com.example.jariBean.repository.tableClass.TableClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.jariBean.dto.manager.ManagerReqDto.ManagerTableReqDto;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final CafeRepository cafeRepository;
    private final TableRepository tableRepository;
    private final TableClassRepository tableClassRepository;
    private final ReservedRepository reservedRepository;

    private final PasswordEncoder passwordEncoder;
    private final CafeManagerRepository cafeManagerRepository;

    public void toggleMatchingStatus(String id) {
        cafeRepository.findById(id)
                .orElseThrow(() -> new CustomDBException("id에 해당하는 Cafe가 존재하지 않습니다."))
                        .toggleMatchingStatus();
    }

    public void updateTable(String tableId, ManagerTableReqDto managerTableReqDto) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new CustomDBException("tableId에 해당하는 Table이 존재하지 않습니다."));

        table.update(managerTableReqDto.getName(), managerTableReqDto.getDescription(), managerTableReqDto.getImage());
        tableRepository.save(table);
    }

    public void addTable(ManagerTableReqDto managerTableReqDto) {
        TableClass tableClass = tableClassRepository.findById(managerTableReqDto.getTableClassId())
                .orElseThrow(() -> new CustomDBException("id에 해당하는 tableClass가 존재하지 않습니다."));

        Table table = Table.builder()
                .cafeId(tableClass.getCafeId())
                .tableClassId(tableClass.getId())
                .seating(tableClass.getSeating())
                .tableOptionList(tableClass.getTableOptionList())
                .build();

        table.update(managerTableReqDto.getName(), managerTableReqDto.getDescription(), managerTableReqDto.getImage());
        tableRepository.save(table);
    }

    public void updateTableClass(String tableClassId, ManagerTableClassReqDto managerTableClassReqDto) {
        TableClass tableClass = tableClassRepository.findById(tableClassId)
                .orElseThrow(() -> new CustomDBException("id에 해당하는 tableClass가 존재하지 않습니다."));

        tableClass.update(managerTableClassReqDto.getName(), managerTableClassReqDto.getSeating(), managerTableClassReqDto.getOption());
        tableClassRepository.save(tableClass);
    }

    public void addTableClass(String cafeId, ManagerTableClassReqDto managerTableClassReqDto) {

        TableClass tableClass = TableClass.builder()
                .cafeId(cafeId)
                .build();

        tableClass.update(managerTableClassReqDto.getName(), managerTableClassReqDto.getSeating(), managerTableClassReqDto.getOption());
        tableClassRepository.save(tableClass);
    }

    public ManagerLoginResDto join(ManagerJoinReqDto managerJoinReqDto) {

        if(cafeManagerRepository.existsByEmail(managerJoinReqDto.getEmail())) {
            throw new CustomDBException("email에 해당하는 cafeManager가 이미 존재합니다.");
        }

        CafeManager cafeManager = managerJoinReqDto.toEntity(passwordEncoder);
        CafeManager savedManager = cafeManagerRepository.save(cafeManager);
        return ManagerLoginResDto.builder()
                .id(savedManager.getId())
                .email(savedManager.getEmail())
                .role(savedManager.getRole().toString())
                .build();
    }

    public List<TableClassDto> getTableClassList(String cafeId) {
        return tableClassRepository.findByCafeId(cafeId).stream()
                .map(TableClassDto::new)
                .collect(Collectors.toList());
    }

    public List<TableDto> getTableList(String tableClassId) {
        return tableRepository.findByTableClassId(tableClassId).stream()
                .map(TableDto::new)
                .collect(Collectors.toList());
    }

    public Map<String, List<ReserveDto>> getReserveList(String tableClassId) {

        // tableClassId로 조회한 Reserve 내역
        List<ReserveDto> findReserveList = reservedRepository.findByTableClassId(tableClassId).stream()
                .map(ReserveDto::new)
                .collect(Collectors.toList());

        // tableId를 key 값으로 Map 만들기
        return findReserveList.stream()
                .collect(Collectors.groupingBy(ReserveDto::getTableId));
    }
}
