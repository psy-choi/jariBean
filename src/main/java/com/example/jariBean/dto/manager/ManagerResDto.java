package com.example.jariBean.dto.manager;

import com.example.jariBean.entity.Reserved;
import com.example.jariBean.entity.Table;
import com.example.jariBean.entity.TableClass;
import com.example.jariBean.entity.TableClass.TableOption;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ManagerResDto {

    @Data
    public static class ManagerReserveResDto {
        private List<TableClassSummaryDto> tableClassSummaryDtoList = new ArrayList<>();
        private List<TableReserveDto> tableReserveDtoList = new ArrayList<>();

        public void addTableClassSummaryDto(TableClass tableClass) {
            TableClassSummaryDto tableClassSummaryDto = new TableClassSummaryDto(tableClass);
            tableClassSummaryDtoList.add(tableClassSummaryDto);
        }

        public void addTableReserveDtoList(TableReserveDto tableReserveDto) {
            tableReserveDtoList.add(tableReserveDto);
        }
    }

    @Data
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    public static class ManagerTableResDto {
        private String id;
        private String name;
        private Integer seating;
        private List<TableOption> option;
        private List<TableSummaryDto> tableSummaryDtoList = new ArrayList<>();

        public void addTableClassSummaryDto(TableClass tableClass) {
            this.id = tableClass.getId();
            this.name = tableClass.getName();
            this.seating = tableClass.getSeating();
            this.option = tableClass.getTableOptionList();
        }

        public void addTableSummaryDto(Table table) {
            TableSummaryDto tableSummaryDto = new TableSummaryDto();
            tableSummaryDto.setId(table.getId());
            tableSummaryDto.setName(table.getName());
            tableSummaryDto.setDescription(table.getDescription());
            tableSummaryDto.setImage(table.getImage());
            tableSummaryDtoList.add(tableSummaryDto);
        }
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    public static class TableClassSummaryDto {
        private String id;
        private String name;
        private Integer seating;
        private String image;
        private List<TableOption> option;
        private List<TableSummaryDto> tableSummaryDtoList;

        public TableClassSummaryDto(TableClass tableClass) {
            this.id = tableClass.getId();
            this.name = tableClass.getName();
            this.seating = tableClass.getSeating();
            this.image = tableClass.getImage();
            this.option = tableClass.getTableOptionList();
        }
    }

    @Data
    public static class TableSummaryDto {
        private String id;
        private String name;
        private String description;
        private String image;
    }

    @Data
    public static class TableReserveDto {
        private String tableId;
        private String tableName;
        private List<ReservePeriodDto> reservePeriodDtoList = new ArrayList<>();

        public void setTable(Table table) {
            this.tableId = table.getId();
            this.tableName = table.getName();
        }

        public void addReservePeriod(Reserved reserved) {
            ReservePeriodDto reservePeriodDto = new ReservePeriodDto();
            reservePeriodDto.setReservePeriodDto(reserved);
            reservePeriodDtoList.add(reservePeriodDto);
        }
    }

    @Data
    public static class ReservePeriodDto {
        private String username;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public void setReservePeriodDto(Reserved reserved) {
            this.username = reserved.getUserId();
            this.startTime = reserved.getStartTime();
            this.endTime = reserved.getEndTime();
        }
    }

    @Data
    public static class ManagerLoginResDto {
        private String id;
        private String email;
        private String role;

        @Builder
        public ManagerLoginResDto(String id, String email, String role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    public static class TableClassDto {
        private String id;
        private String name;
        private String image;
        private int seating;
        private List<TableOption> tableOptionList;

        public TableClassDto(TableClass tableClass) {
            this.id = tableClass.getId();
            this.name = tableClass.getName();
            this.image = tableClass.getImage();
            this.seating = tableClass.getSeating();
            this.tableOptionList = tableClass.getTableOptionList();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    public static class TableDto {
        private String id;
        private String name;
        private String image;
        private String description;

        public TableDto(Table table) {
            this.id = table.getId();
            this.name = table.getName();
            this.image = table.getImage();
            this.description = table.getDescription();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    public static class ReserveDto {
        private String id;
        private String userId;
        private String tableId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public ReserveDto(Reserved reserved) {
            this.id = reserved.getId();
            this.userId = reserved.getUserId();
            this.tableId = reserved.getTable().getId();
            this.startTime = reserved.getStartTime();
            this.endTime = reserved.getEndTime();
        }
    }



}