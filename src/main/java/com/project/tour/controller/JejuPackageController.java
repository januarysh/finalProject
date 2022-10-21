package com.project.tour.controller;

import com.project.tour.domain.*;
import com.project.tour.domain.Package;
import com.project.tour.service.PackageService;
import com.project.tour.service.PackageDateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/jeju")
public class JejuPackageController {

    @Autowired
    private final PackageService packageService;
    @Autowired
    private final PackageDateService packagedateService;

    @ModelAttribute("transports")
    public Map<String, String> transport() {
        Map<String, String> transport = new LinkedHashMap<>();
        transport.put("대한항공", "대한항공");
        transport.put("아시아나항공", "아시아나항공");
        transport.put("제주항공", "제주항공");
        transport.put("진에어", "진에어");
        transport.put("티웨이", "티웨이");
        return transport;
    }

    @ModelAttribute("travelPeriods")
    public Map<String, String> travelPeriod() {
        Map<String, String> travelPeriod = new LinkedHashMap<>();
        travelPeriod.put("1,2", "2일 ~ 3일");
        travelPeriod.put("3,4", "4일 ~ 5일");
        travelPeriod.put("5,6", "6일 ~ 7일");
        travelPeriod.put("7,8,9", "8일 ~ 10일");
        return travelPeriod;
    }

    /**
     * 전체리스트
     */
    @GetMapping("/list")
    public String packagelist(@RequestParam(value = "location", required = false) String location,
                              @RequestParam(value = "date", required = false) String date,
                              @RequestParam(value = "totcount", required = false) Integer count,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "transports", required = false) List<String> transports,
                              @RequestParam(value = "travelPeriods", required = false) String travelPeriods,
                              Model model, @PageableDefault Pageable pageable, SearchForm searchForm) {

        //여행객 버튼 기본값 0출력
        if(searchForm.getTotcount() == null || searchForm.getTotcount().equals("")){
            searchForm.setTotcount(0);
        }

        //날짜가 선택되지 않았을때
        if (date == null || date.equals("")) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            date = today.format(formatter);

        } else {//날짜포맷 db와 맞추기
            date = date.replaceAll("-", "");
        }

        //여행기간
        List<Integer> period = new ArrayList<>();
        List<String> periods = null;

        if(travelPeriods!=null){
            periods = Arrays.asList(travelPeriods.split(","));

            Iterator<String> it = periods.iterator();

            while(it.hasNext()){
                period.add(Integer.parseInt(it.next()));
                }
        }

        log.info("DATE : " + date);
        log.info("LOCATION : " + location);
        log.info("COUNT : " + String.valueOf(count));
        log.info("KEYWORD : " + keyword);
        log.info("TRANSPORTS : " + transports);
        log.info("TRAVELPERIOD : " + travelPeriods);

        Page<Package> paging = packageService.getSearchList(location, date, count,keyword,transports,period,pageable);

        model.addAttribute("paging", paging);
        model.addAttribute("searchForm", searchForm);

        return "jejuPackage/packagelist";
    }

    /**
     * 상세페이지
     */
    @GetMapping("/{id}")
    public String packagedetail(@PathVariable("id") Long id, Model model) {

        Package apackage = packageService.getPackage(id);

        model.addAttribute("package", apackage);
        model.addAttribute("bookingform", new BookingDTO());
        return "jejuPackage/packagedetail";
    }

    /**
     * 상세페이지 여행날짜별 가격출력
     */
    @GetMapping("/dateprice")
    @ResponseBody
    public HashMap<String, Object> datecountprice(@RequestParam("acount") Integer acount, @RequestParam("ccount") Integer ccount,
                                                  @RequestParam("bcount") Integer bcount, @RequestParam("date") String date,
                                                  @RequestParam("packagenum") Long packagenum) {

        HashMap<String, Object> priceInfo = new HashMap<String, Object>();
        int aprice, bprice, cprice, dcaprice, dcbprice, dccprice;

        date = date.replaceAll("-", "");

        log.info(date.getClass().getTypeName());

        /* 해당 날짜에 어른/아이/유아 타입별 가격*/
        PackageDate getPackagePrice = packagedateService.getPrice(packagenum, date);
        Integer discount = getPackagePrice.getDiscount();

        /** 정가 */
        aprice = getPackagePrice.getAprice() * acount;
        bprice = getPackagePrice.getBprice() * bcount;
        cprice = getPackagePrice.getCprice() * ccount;

        if (getPackagePrice.getDiscount() == null) {

        } else {/** 할인가 */

            dcaprice = (int) (aprice - (aprice * (discount * 0.01)));
            dcbprice = (int) (bprice - (bprice * (discount * 0.01)));
            dccprice = (int) (cprice - (cprice * (discount * 0.01)));
            priceInfo.put("dcaprice", dcaprice);
            priceInfo.put("dcbprice", dcbprice);
            priceInfo.put("dccprice", dccprice);
        }

        //json형태 데이터로 넘기기
        priceInfo.put("acount", acount);
        priceInfo.put("aprice", aprice);
        priceInfo.put("ccount", ccount);
        priceInfo.put("cprice", cprice);
        priceInfo.put("bcount", bcount);
        priceInfo.put("bprice", bprice);
        priceInfo.put("discount", discount);

        return priceInfo;
    }

}
