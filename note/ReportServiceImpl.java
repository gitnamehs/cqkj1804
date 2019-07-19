package com.qxuninfo.dadi.service.report.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.qxuninfo.common.RestResponse;
import com.qxuninfo.dadi.domain.report.PersonnelList;
import com.qxuninfo.dadi.domain.report.SubAgency;
import com.qxuninfo.dadi.dto.RankList;
import com.qxuninfo.dadi.repository.report.PersonnelListRepository;
import com.qxuninfo.dadi.repository.report.SubAgencyRepository;
import com.qxuninfo.dadi.service.report.ReportService;
import com.qxuninfo.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.Cell.*;

/**
 * Description 打假增效报表
 * Created by xsq on 2019/5/31
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    private static final int PERSONNEL = 9;

    private static final int AGENCY = 38;

    @Resource
    private SubAgencyRepository agencyRepository;

    @Resource
    private PersonnelListRepository listRepository;

    /**
     * Description 数据入库
     *
     * @param file
     */
    @Override
    public RestResponse recordDate(MultipartFile file) {
        RestResponse restResponse = new RestResponse();
        InputStream in = null;
        //工作簿
        Workbook workbook = null;
        try {
            in = file.getInputStream();
            String prefix = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            //校验文件类型
            if (prefix.toLowerCase().equals("xlsx")) {
                workbook = new XSSFWorkbook(in);
            } else if (prefix.toLowerCase().equals("xls")) {
                workbook = new HSSFWorkbook(in);
            } else {
                log.info("不是excel文件!");
                restResponse.setCode(500);
                restResponse.setMessage("不是excel文件");
                return restResponse;
            }
            // 打开Excel中的第一个Sheet
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int rowNum = sheet.getLastRowNum();
            //总列数
            int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
            int i = 0;
            //加工sheet数据
            List<List<String>> container = buildData(sheet);
            if (coloumNum <= PERSONNEL) {
                //判断库中是否有这周的数据
                final int week = getWeekOfYear();
                Specification<PersonnelList> specification = new Specification<PersonnelList>() {
                    @Override
                    public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return cb.equal(root.get("week"), week);
                    }
                };
                List<PersonnelList> personnelLists1 = listRepository.findAll(specification);
                if (personnelLists1.size() > 0) {
                    //将list按指定大小切割
                    List<List<PersonnelList>> partition = Lists.partition(personnelLists1, 500);
                    for (List<PersonnelList> personnelListList : partition) {
                        listRepository.deleteInBatch(personnelListList);
                    }
                    personnelLists1.clear();
                    partition.clear();
                }
                List<PersonnelList> personnelLists = new ArrayList<PersonnelList>();
                for (List<String> stringList : container) {
                    PersonnelList personnelList = new PersonnelList();
                    personnelList.setId(UUIDUtil.getUUID());
                    personnelList.setPersonnelCode(stringList.get(0));
                    personnelList.setPersonnelName(stringList.get(1));
                    personnelList.setSubCompany(stringList.get(2));
                    personnelList.setThreeLevelAgency(stringList.get(3));
                    personnelList.setFourLevelAgency(stringList.get(4));
                    personnelList.setPostName(stringList.get(5));
                    personnelList.setReduceNum(stringList.get(6));
                    personnelList.setReduceCompensation(stringList.get(7));
                    personnelList.setWeek(getWeekOfYear());
                    personnelList.setCreateTime(new Date());
                    personnelList.setUpdateTime(new Date());

                    personnelLists.add(personnelList);
                }
                personnelLists.remove(0);
                //批量新增
                if (!ObjectUtils.isEmpty(personnelLists)) {
                    listRepository.save(personnelLists);
                    personnelLists.clear();
                }
            } else if (coloumNum >= AGENCY) {
                //判断库中是否有这周的数据
                final int week = getWeekOfYear();
                Specification<SubAgency> specification = new Specification<SubAgency>() {
                    @Override
                    public Predicate toPredicate(Root<SubAgency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return cb.equal(root.get("week"), week);
                    }
                };
                List<SubAgency> subAgencyList = agencyRepository.findAll(specification);
                if (subAgencyList.size() > 0) {
                    //list切割
                    List<List<SubAgency>> partition = Lists.partition(subAgencyList, 500);
                    for (List<SubAgency> subAgencyList1 : partition) {
                        agencyRepository.deleteInBatch(subAgencyList1);
                    }
                    subAgencyList.clear();
                    partition.clear();
                }
                List<SubAgency> agencyList = new ArrayList<SubAgency>();
                for (List<String> stringList : container) {
                    SubAgency agency = new SubAgency();
                    agency.setId(UUIDUtil.getUUID());
                    agency.setAgentCode(stringList.get(0));
                    agency.setAgentName(stringList.get(1));
                    agency.setSumNumber(stringList.get(2));
                    agency.setIncrMoney(stringList.get(3));
                    agency.setParticipation(stringList.get(4));
                    agency.setIncrChallenge(stringList.get(5));
                    agency.setIncrChallengePreTime(stringList.get(6));
                    agency.setIncrEnsure(stringList.get(7));
                    agency.setIncrEnsurePreTime(stringList.get(8));
                    agency.setVehicle(stringList.get(9));
                    agency.setVehChallenge(stringList.get(10));
                    agency.setVehChallengePreTime(stringList.get(11));
                    agency.setVehEnsure(stringList.get(12));
                    agency.setVehEnsurePreTime(stringList.get(13));
                    agency.setInjury(stringList.get(14));
                    agency.setInChallenge(stringList.get(15));
                    agency.setInChallengePreTime(stringList.get(16));
                    agency.setInEnsure(stringList.get(17));
                    agency.setInEnsurePreTime(stringList.get(18));
                    agency.setAccInsurance(stringList.get(19));
                    agency.setAccChallenge(stringList.get(20));
                    agency.setAccChallengePreTime(stringList.get(21));
                    agency.setAccEnsure(stringList.get(22));
                    agency.setAccEnsurePreTime(stringList.get(23));
                    agency.setSpecTicket(stringList.get(24));
                    agency.setSpecChallenge(stringList.get(25));
                    agency.setSpecChallengePreTime(stringList.get(26));
                    agency.setSpecEnsure(stringList.get(27));
                    agency.setSpecEnsurePreTime(stringList.get(28));
                    agency.setLossRecovery(stringList.get(29));
                    agency.setLossChallenge(stringList.get(30));
                    agency.setLossChallengePreTime(stringList.get(31));
                    agency.setLossEnsure(stringList.get(32));
                    agency.setLossEnsurePreTime(stringList.get(33));
                    agency.setPartsRepair(stringList.get(34));
                    agency.setPartsChallenge(stringList.get(35));
                    agency.setPartsChallengePreTime(stringList.get(36));
                    agency.setPartsEnsure(stringList.get(37));
                    agency.setPartsEnsurePreTime(stringList.get(38));
                    agency.setIsTotal(0);
                    agency.setWeek(getWeekOfYear());
                    agency.setCreateTime(new Date());
                    agency.setUpdateTime(new Date());
                    if (i == rowNum) {
                        agency.setIsTotal(1);
                    }
                    i++;
                    agencyList.add(agency);
                }
                agencyList.remove(0);
                //批量新增
                if (!ObjectUtils.isEmpty(agencyList)) {
                    agencyRepository.save(agencyList);
                    agencyList.clear();
                }
            }
            container.clear();
            restResponse.setCode(200);
            restResponse.setMessage("上传成功！");
        } catch (Throwable e) {
            e.printStackTrace();
            log.info("excel解析异常:" + e.getMessage());
            restResponse.setCode(500);
            restResponse.setMessage("上传异常！");
        } finally {
            IOUtils.closeQuietly(in);
        }
        return restResponse;
    }

    /**
     * Description 红榜
     */
    @Override
    public RestResponse findRedList() {
        RestResponse restResponse = new RestResponse();
        //获取当前是第几周
        final int week = getWeekOfYear();
        //上一周
        final int lastWeek = getWeekOfYear() - 1;
        //获取当前这一周的数据
        Specification<PersonnelList> specification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("week"), week);
            }
        };
        List<PersonnelList> personnelLists = listRepository.findAll(specification);
        //将查询出来的数据重新放到类型为RankList的集合中
        List<RankList> formerRankLists = new ArrayList<RankList>();
        if (!ObjectUtils.isEmpty(personnelLists)) {
            //降序
            Collections.sort(personnelLists, new Comparator<PersonnelList>() {
                @Override
                public int compare(PersonnelList s1, PersonnelList s2) {
                    Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                    Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());

                    return num2.compareTo(num1);
                }
            });
            int k = 0;
            for (PersonnelList list : personnelLists) {
                RankList rank = new RankList();
                rank.setSubCompany(list.getSubCompany());
                rank.setFourLevelAgency(list.getFourLevelAgency());
                rank.setThreeLevelAgency(list.getThreeLevelAgency());
                rank.setPersonnelName(list.getPersonnelName());
                rank.setPersonnelCode(list.getPersonnelCode());
                rank.setPostName(list.getPostName());
                rank.setReduceCompensation(list.getReduceCompensation());
                //将rank对象保存在集合
                formerRankLists.add(rank);
                k++;
                if (k >= 20) {
                    break;
                }
            }
            personnelLists.clear();
        }
        //获取上一周的数据
        Specification<PersonnelList> lastSpecification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("week"), lastWeek);
            }
        };
        //用list集合保存数据
        List<PersonnelList> lastPersonnelLists = listRepository.findAll(lastSpecification);
        if (!ObjectUtils.isEmpty(lastPersonnelLists)) {
            //降序按照打假金额
            Collections.sort(lastPersonnelLists, new Comparator<PersonnelList>() {
                @Override
                public int compare(PersonnelList s1, PersonnelList s2) {
                    Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                    Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());
                    return num2.compareTo(num1);
                }
            });
            //根据工号将上一周的排名与这周的排名进行比较。
            //并且将排名变化保存在ranlist的ranchange里面
            for (int i = 0; i < formerRankLists.size(); i++) {

                for (int j = 0; j < lastPersonnelLists.size(); j++) {
                    if (StringUtils.isEmpty(formerRankLists.get(i).getPersonnelCode()) ||
                            StringUtils.isEmpty(lastPersonnelLists.get(j).getPersonnelCode())) {
                        formerRankLists.get(i).setRankChange(0);
                        break;
                    } else if (formerRankLists.get(i).getPersonnelCode().equals(lastPersonnelLists.get(j).getPersonnelCode())) {
                        //根据工号去匹对，获取排名变化
                        formerRankLists.get(i).setRankChange(j - i);
                        break;
                    }
                }
            }
            lastPersonnelLists.clear();
        }
        //将前二十条数据返回
        restResponse.setData(formerRankLists);
        if (ObjectUtils.isEmpty(formerRankLists)) {
            restResponse.setData(null);
        }
        restResponse.setCode(200);
        return restResponse;
    }

    /**
     * Description 达成率
     */
    @Override
    public RestResponse reachRate() {
        RestResponse restResponse = new RestResponse();
        Map map = new HashMap();
        final int week = getWeekOfYear();
        Specification<SubAgency> specification = new Specification<SubAgency>() {
            @Override
            public Predicate toPredicate(Root<SubAgency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("week"), week);
            }
        };
        List<SubAgency> agencyList = agencyRepository.findAll(specification);
        List<SubAgency> agencyList5 = new ArrayList<SubAgency>();
        List<SubAgency> agencyList8 = new ArrayList<SubAgency>();
        List<SubAgency> agencyList9 = new ArrayList<SubAgency>();
        for (SubAgency subAgency : agencyList) {
            if (StringUtils.isEmpty(subAgency.getIncrChallengePreTime()) ||
                    "总公司".equals(subAgency.getAgentName())) {
                continue;
            }
            double rate = Double.valueOf(subAgency.getIncrChallengePreTime());
            if (rate <= 0.5) {
                agencyList5.add(subAgency);
            } else if (rate > 0.5 && rate <= 0.8) {
                agencyList8.add(subAgency);
            } else if (rate > 0.8) {
                agencyList9.add(subAgency);
            }
        }
        map.put("minLev", agencyList5);
        map.put("avgLev", agencyList8);
        map.put("maxLev", agencyList9);
        restResponse.setCode(200);
        restResponse.setData(map);
        return restResponse;
    }

    /**
     * Description 分项
     */
    @Override
    public RestResponse subItem(final String agentName) {
        RestResponse restResponse = new RestResponse();
        final int week = getWeekOfYear();
        Specification<SubAgency> specification = new Specification<SubAgency>() {
            @Override
            public Predicate toPredicate(Root<SubAgency> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), week));
                predicates.add(cb.equal(root.get("agentName"), agentName));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        SubAgency subAgency = agencyRepository.findOne(specification);
        restResponse.setCode(200);
        restResponse.setData(subAgency);
        return restResponse;
    }

    /**
     * Description 人员
     *
     * @param agentName
     */
    @Override
    public RestResponse personelList(final String agentName) {
        RestResponse restResponse = new RestResponse();
        //当前周
        final int week = getWeekOfYear();
        Specification<PersonnelList> specification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), week));
                predicates.add(cb.equal(root.get("subCompany"), agentName));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<PersonnelList> personnelLists = listRepository.findAll(specification);
        //将查询出来的数据重新放到类型为RankList的集合中
        List<RankList> formerRankLists = new ArrayList<RankList>();
        if (!ObjectUtils.isEmpty(personnelLists)) {
            //按照打假金额降序
            Collections.sort(personnelLists, new Comparator<PersonnelList>() {
                @Override
                public int compare(PersonnelList s1, PersonnelList s2) {
                    Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                    Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());

                    return num2.compareTo(num1);
                }
            });
            for (PersonnelList list : personnelLists) {
                RankList rank = new RankList();
                rank.setSubCompany(list.getSubCompany());
                rank.setFourLevelAgency(list.getFourLevelAgency());
                rank.setThreeLevelAgency(list.getThreeLevelAgency());
                rank.setPersonnelName(list.getPersonnelName());
                rank.setPersonnelCode(list.getPersonnelCode());
                rank.setPostName(list.getPostName());
                rank.setReduceCompensation(list.getReduceCompensation());
                //将rank对象保存在集合
                formerRankLists.add(rank);
            }

        }
        //获取第几周
        final int lastWeeks = getWeekOfYear() - 1;
        Specification<PersonnelList> lastSpecification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), lastWeeks));
                predicates.add(cb.equal(root.get("subCompany"), agentName));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        //上一周数据
        List<PersonnelList> lastPersonnelLists = listRepository.findAll(lastSpecification);
        if (!ObjectUtils.isEmpty(lastPersonnelLists)) {
            //降序按照打假金额
            Collections.sort(lastPersonnelLists, new Comparator<PersonnelList>() {
                @Override
                public int compare(PersonnelList s1, PersonnelList s2) {
                    Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                    Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());
                    return num2.compareTo(num1);
                }
            });
            //根据工号将上一周的排名与这周的排名进行比较。
            //并且将排名变化保存在ranlist的ranchange里面
            for (int i = 0; i < formerRankLists.size(); i++) {

                for (int j = 0; j < lastPersonnelLists.size(); j++) {
                    if (StringUtils.isEmpty(formerRankLists.get(i).getPersonnelCode()) ||
                            StringUtils.isEmpty(lastPersonnelLists.get(j).getPersonnelCode())) {
                        formerRankLists.get(i).setRankChange(0);
                        break;
                    } else if (formerRankLists.get(i).getPersonnelCode().equals(lastPersonnelLists.get(j).getPersonnelCode())) {
                        //根据工号去匹对，获取排名变化
                        formerRankLists.get(i).setRankChange(j - i);
                        break;
                    }
                }
            }
        }
        //将前二十条数据返回
        if (ObjectUtils.isEmpty(formerRankLists)) {
            restResponse.setData(null);
        } else if (formerRankLists.size() <= 20) {
            restResponse.setData(formerRankLists);
        } else {
            restResponse.setData(formerRankLists.subList(0, 20));
        }
        restResponse.setCode(200);
        return restResponse;
    }

    /**
     * Description 搜索
     *
     * @param
     */
    @Override
    public RestResponse searchPerson(final String personnelCode) {
        RestResponse restResponse = new RestResponse();
        final int week = getWeekOfYear();
        Specification<PersonnelList> specification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), week));
                predicates.add(cb.equal(root.get("personnelCode"), personnelCode));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<PersonnelList> personnelLists = listRepository.findAll(specification);
        if (ObjectUtils.isEmpty(personnelLists)) {
            restResponse.setCode(200);
            restResponse.setData(null);
            return restResponse;
        }
        //将查询出来的数据重新放到类型为RankList的集合中
        List<RankList> formerRankLists = new ArrayList<RankList>();
        RankList rankList = new RankList();
        BeanUtils.copyProperties(personnelLists.get(0), rankList);
        formerRankLists.add(rankList);

        //本周数据
        final int thisWeeks = getWeekOfYear();
        Specification<PersonnelList> thisSpecification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), thisWeeks));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<PersonnelList> thisPersonnelLists = listRepository.findAll(thisSpecification);
        //按照打假金额降序
        Collections.sort(thisPersonnelLists, new Comparator<PersonnelList>() {
            @Override
            public int compare(PersonnelList s1, PersonnelList s2) {
                Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());

                return num2.compareTo(num1);
            }
        });

        //上周数据
        final int lastWeeks = getWeekOfYear() - 1;
        Specification<PersonnelList> lastSpecification = new Specification<PersonnelList>() {
            @Override
            public Predicate toPredicate(Root<PersonnelList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("week"), lastWeeks));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<PersonnelList> lastPersonnelLists = listRepository.findAll(lastSpecification);
        //按照打假金额降序
        Collections.sort(lastPersonnelLists, new Comparator<PersonnelList>() {
            @Override
            public int compare(PersonnelList s1, PersonnelList s2) {
                Double num1 = StringUtils.isEmpty(s1.getReduceCompensation()) ? 0 : Double.parseDouble(s1.getReduceCompensation());
                Double num2 = StringUtils.isEmpty(s2.getReduceCompensation()) ? 0 : Double.parseDouble(s2.getReduceCompensation());

                return num2.compareTo(num1);
            }
        });

        if (!ObjectUtils.isEmpty(personnelLists) && !ObjectUtils.isEmpty(lastPersonnelLists)) {
            PersonnelList personnelList = personnelLists.get(0);
            //获取此用户在本周的序号
            int index = thisPersonnelLists.indexOf(personnelList);
            for (int j = 0; j < lastPersonnelLists.size(); j++) {
                if (!StringUtils.isEmpty(lastPersonnelLists.get(j).getPersonnelCode())
                        && personnelList.getPersonnelCode().equals(lastPersonnelLists.get(j).getPersonnelCode())) {
                    formerRankLists.get(0).setRankChange(j - index);
                    break;
                }
            }
            thisPersonnelLists.clear();
            lastPersonnelLists.clear();
        }
        restResponse.setCode(200);
        restResponse.setData(formerRankLists);
        return restResponse;
    }

    private int getWeekOfYear() {
//        String today = "2019-01-14";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = format.parse(today);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(new Date());
        } catch (Exception e) {

        }
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private List<List<String>> buildData(Sheet sheet) {
        List<List<String>> container = new ArrayList<List<String>>();
        for (Row row : sheet) { // 行
            List<String> stringList = new ArrayList<String>();
            int i = 0;
            for (Cell cell : row) { // 单元格
                stringList.add(getCellValue(cell, i));
                i++;
            }
            container.add(stringList);
        }
        return container;
    }

    private String getCellValue(Cell cell, int i) {
        String value = "";
        switch (cell.getCellType()) { // 不同的数据类型
            case CELL_TYPE_STRING:// 字符串类型
                value = cell.getStringCellValue();
                break;
            case CELL_TYPE_NUMERIC:// 数值类型
                if (i == 0) {//i:0处理工号数据
                    DecimalFormat df = new DecimalFormat("0");
                    value = df.format(cell.getNumericCellValue());
                } else {
                    value = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case CELL_TYPE_BOOLEAN:// 布尔类型
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case CELL_TYPE_FORMULA:// 公式类型
                value = cell.getCellFormula();
                break;
            case CELL_TYPE_BLANK:// 空白类型
                value = "";
                break;
            default:
                break;
        }
        return value;
    }


}
