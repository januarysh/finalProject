package com.project.tour.service;

import com.project.tour.domain.EstimateInquiry;
import com.project.tour.domain.Member;
import com.project.tour.domain.QnA;
import com.project.tour.domain.Review;
import com.project.tour.repository.EstimateRepository;
import com.project.tour.repository.MemberRepository;
import com.project.tour.repository.QnARepository;
import com.project.tour.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final ReviewRepository reviewRepository;

    private final EstimateRepository estimateRepository;

    private final QnARepository qnARepository;

    private final MemberRepository memberRepository;

    //리뷰
    public List<Review> getMypageReview(Long id){

        List<Review> op = reviewRepository.findByAuthor_Id(id);

        return op;


    }

    //견적문의
    public Page<EstimateInquiry> getMypageEstimate(String email, Pageable pageable){

        List<Sort.Order> sort = new ArrayList<Sort.Order>();
        sort.add(Sort.Order.desc("id")); //MemberId

        pageable = PageRequest.of(
                pageable.getPageNumber() <= 0 ?
                        0 : pageable.getPageNumber() -1, //반환할 페이지
                pageable.getPageSize(), //반환할 리스트 갯수
                Sort.by(sort)); //정렬 매개변수 적용


        return  estimateRepository.findByEmail(email,pageable);
    }

    //QnA
    public Page<QnA> getMypageQnA(Long id,Pageable pageable){

        List<Sort.Order> sort = new ArrayList<Sort.Order>();
        sort.add(Sort.Order.desc("id")); //MemberId

        pageable = PageRequest.of(
                pageable.getPageNumber() <= 0 ?
                        0 : pageable.getPageNumber() -1, //반환할 페이지
                pageable.getPageSize(), //반환할 리스트 갯수
                Sort.by(sort)); //정렬 매개변수 적용

        return qnARepository.findByMember_Id(id,pageable);

    }

    //비밀번호 변경
    public void updatePwd(Member member, String password){

        member.setPassword(password);

        memberRepository.save(member);

    }


}
