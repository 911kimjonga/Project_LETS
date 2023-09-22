package com.vj.lets.web.group.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vj.lets.domain.article.dto.Article;
import com.vj.lets.domain.article.dto.ArticleComment;
import com.vj.lets.domain.article.dto.ArticleCreateForm;
import com.vj.lets.domain.article.service.ArticleCommentService;
import com.vj.lets.domain.article.service.ArticleService;
import com.vj.lets.domain.group.dto.*;
import com.vj.lets.domain.group.service.StudyGroupService;
import com.vj.lets.domain.group.util.PageParams;
import com.vj.lets.domain.group.util.Pagination;
import com.vj.lets.domain.location.dto.SiGunGu;
import com.vj.lets.domain.location.service.SiGunGuService;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 스터디 그룹 컨트롤러
 *
 * @author VJ특공대 이희영
 * @author VJ특공대 이한솔
 * @version 1.0
 * @since 2023-09-11 (월)
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/group")
@Slf4j
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final SiGunGuService siGunGuService;
    private final MemberService memberService;

    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;

    /**
     * 실제 회원 이미지 경로
     */
    @Value("${group.imageLocation}")
    private String imageLocation;

    /**
     * DB에 입력할 회원 이미지 경로
     */
    @Value("${group.imageDBPath}")
    private String imageDBPath;

    /**
     * 실제 게시글 이미지 경로
     */
    @Value("${article.imageLocation}")
    private String articleImageLocation;

    /**
     * DB에 입력할 게시글 이미지 경로
     */
    @Value("${article.imageDBPath}")
    private String articleImageDBPath;

    /**
     * 스터디 전체 리스트 화면 출력
     *
     * @param page    페이지
     * @param keyword 검색 키워드
     * @param model   모델 인터페이스
     * @return 스터디 리스트 화면
     * @author VJ특공대 이희영
     */
    @GetMapping("")
    public String studyGroup(@PathParam("page") String page, @PathParam("keyword") String keyword, @PathParam("subject") String subject, @PathParam("siGunGuName") String siGunGuName, Model model) {
        int count = studyGroupService.getSearchCount(keyword);
        int elementSize = 8;
        int pageSize = 5;

        if (page == null || page.isEmpty()) {
            page = "1";
        }

        int selectPage = Integer.parseInt(page);

        PageParams pageParams = PageParams.builder()
                .elementSize(elementSize)
                .pageSize(pageSize)
                .requestPage(selectPage)
                .rowCount(count)
                .keyword(keyword)
                .subject(subject)
                .siGunGuName(siGunGuName)
                .build();

        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> studyGroupList = studyGroupService.getStudyList(pageParams);
        List<StudyGroup> newStudyList = studyGroupService.getNewStudyList();
        model.addAttribute("pagination", pagination);
        model.addAttribute("studyGroupList", studyGroupList);
        model.addAttribute("newStudyList", newStudyList);

        return "common/group/group_list";
    }

    /**
     * 스터디 그룹 상세보기
     *
     * @param page        게시글 페이지
     * @param keyword     게시글 검색 키워드
     * @param id          스터디 그룹 아이디
     * @param loginMember 로그인 회원 정보
     * @param model       모델 인터페이스
     * @return 스터디 그룹 상세
     * @author VJ특공대 이희영
     * @author VJ특공대 이한솔
     */
    @GetMapping("/{id}")
    public String readGroup(@PathVariable int id, @RequestParam(value = "page", required = false) String page, @RequestParam(value = "keyword", required = false) String keyword, @SessionAttribute Member loginMember, Model model) {
        // 이희영
        GroupMemberList groupMember = null;
        List<Map<String, Object>> contactList = null;

        Map<String, Object> studyGroup = studyGroupService.viewStudy(id);
        if (studyGroupService.isGroupMember(loginMember.getId(), id) != null) {
            groupMember = studyGroupService.isGroupMember(loginMember.getId(), id);
        }

        if (studyGroupService.isGroupMember(loginMember.getId(), id) != null && studyGroupService.isGroupMember(loginMember.getId(), id).getPosition().equals("팀장")) {
            contactList = studyGroupService.getStudyContactList(id);
        }

        Member member = memberService.getMember(loginMember.getId());

        model.addAttribute("member", member);
        model.addAttribute("studyGroup", studyGroup);
        model.addAttribute("groupMember", groupMember);
        model.addAttribute("contactList", contactList);


        // 게시글 화면 (이한솔)
        int elementSize = 5;
        int pageSize = 5;

        int count = articleService.getCountAll(keyword, id);

        if (page == null || page.isEmpty()) {
            page = "1";
        }
        int selectPage = Integer.parseInt(page);
        PageParams pageParams = PageParams.builder()
                .elementSize(elementSize)
                .pageSize(pageSize)
                .requestPage(selectPage)
                .rowCount(count)
                .keyword(keyword)
                .build();

        Pagination pagination = new Pagination(pageParams);
        model.addAttribute("pagination", pagination);

        List<Map<String, Object>> articleList = articleService.findByPage(pageParams, id);
        model.addAttribute("articleList", articleList);
        List<Integer> articleIds = new ArrayList<>();
        for (Map<String, Object> articleMap : articleList) {
            int articleId = Integer.parseInt(articleMap.get("ID").toString());
            articleIds.add(articleId);
            log.info("{}", articleIds);
        }

        //해당 게시글의 댓글 목록
        List<Map<String, Object>> articleComments = articleService.findComment(articleIds);
        model.addAttribute("commentList", articleComments);
        log.info("{}", articleComments);

        // 최근 게시글 목록
        List<Article> recentArticles = articleService.getRecentArticles(id);
        model.addAttribute("recentArticleList", recentArticles);

        return "common/group/mygroup";
    }

    /**
     * 스터디 그룹 멤버 관리 화면 출력
     *
     * @param id 스터디 그룹 아이디
     * @return 스터디 그룹 회원 리스트
     * @throws JsonProcessingException Json 데이터 예외
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @RequestMapping("/groupSetting/{id}")
    public String groupMemberModal(@PathVariable int id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> memberList = studyGroupService.getStudyMemberList(id);

        return objectMapper.writeValueAsString(memberList);
    }

    /**
     * 스터디 그룹 멤버 탈퇴
     *
     * @param id       스터디 그룹 아이디
     * @param memberId 회원 아이디
     * @return 탈퇴 성공 유무
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @DeleteMapping("/{id}/{memberId}")
    public String removeMemberModal(@PathVariable int id, @PathVariable int memberId) {
        studyGroupService.studySubtractMember(memberId, id);
        GroupMemberList groupMemberList = studyGroupService.isGroupMember(memberId, id);

        if (groupMemberList == null) {
            return "delete-success";
        } else {
            return "delete-failure";
        }
    }

    /**
     * 스터디 그룹 신청 내역 화면 출력
     *
     * @param id 스터디 그룹 아이디
     * @return 스터디 그룹 가입 신청 리스트
     * @throws JsonProcessingException Json 데이터 예외
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @RequestMapping("/contactSetting/{id}")
    public String groupContactModal(@PathVariable int id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> contactList = studyGroupService.getStudyContactList(id);

        return objectMapper.writeValueAsString(contactList);
    }

    /**
     * 스터디 그룹 가입 신청 승인
     *
     * @param studyGroupId 스터디 그룹 아이디
     * @param id           회원 아이디
     * @return 가입 승인 성공 / 실패 메세지
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @PutMapping("/memberContact/{studyGroupId}/{id}")
    public String groupContactApprove(@PathVariable int studyGroupId, @PathVariable int id) {
        studyGroupService.approveStudyContact(id, studyGroupId);
        GroupMemberList groupMemberList = studyGroupService.isGroupMember(id, studyGroupId);

        if (groupMemberList != null) {
            return "approve-success";
        } else {
            return "approve-failure";
        }
    }

    /**
     * 스터디 그룹 가입 신청 거절
     *
     * @param studyGroupId 스터디 그룹 아이디
     * @param id           회원 아이디
     * @return 가입 거절 성공 / 실패 메세지
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @DeleteMapping("/memberContact/{studyGroupId}/{id}")
    public String groupContactRefuse(@PathVariable int studyGroupId, @PathVariable int id) {
        studyGroupService.refuseStudyContact(id, studyGroupId);
        GroupMemberList groupMemberList = studyGroupService.isGroupMember(id, studyGroupId);

        if (groupMemberList == null) {
            return "refuse-success";
        } else {
            return "refuse-failure";
        }
    }

    /**
     * 내 스터디 리스트 조회 화면
     *
     * @param model 모델 인터페이스
     * @return 가입한 스터디 그룹 리스트
     * @author VJ특공대 이희영
     */
    @GetMapping("/mygroup")
    public String myGroup(@PathParam("page") String page, @SessionAttribute Member loginMember, Model model) {
        int count = studyGroupService.getMyStudyCount(loginMember.getId());
        int elementSize = 7;
        int pageSize = 5;

        if (page == null || page.isEmpty()) {
            page = "1";
        }

        int selectPage = Integer.parseInt(page);

        PageParams pageParams = PageParams.builder()
                .elementSize(elementSize)
                .pageSize(pageSize)
                .requestPage(selectPage)
                .rowCount(count)
                .build();

        List<Map<String, Object>> myStudyListAndPageParams = studyGroupService.getMyStudyListAndPageParams(loginMember.getId(), pageParams);

        Pagination pagination = new Pagination(pageParams);

        model.addAttribute("pagination", pagination);
        model.addAttribute("myStudyList", myStudyListAndPageParams);

        return "common/group/mygroup_list";
    }

    /**
     * 스터디 그룹 생성
     *
     * @param loginMember 로그인 회원 정보
     * @return 스터디 그룹 상세
     * @author VJ특공대 이희영
     */
    @PostMapping("/create")
    public String createGroup(@ModelAttribute CreateForm createForm, MultipartFile imagePath, @SessionAttribute Member loginMember) throws IOException {
        String selectedSubject = createForm.getSubject();
        String subject = subjectChange(selectedSubject);

        StudyGroup studyGroup = StudyGroup.builder()
                .name(createForm.getName())
                .totalCount(createForm.getTotalCount())
                .imagePath(imageDBPath + "default.png")
                .subject(subject)
                .build();

        int studyGroupId = studyGroupService.generateStudy(studyGroup, loginMember.getId(), createForm.getSiGunGuName(), createForm.getSiDoName());
        return "redirect:/group/" + studyGroupId;
    }

    /**
     * 스터디 그룹 정보 수정
     *
     * @param createForm 정보 수정 Form에서 입력된 객체
     * @param id         스터디 그룹 아이디
     * @return 스터디 그룹 상세 화면
     * @author VJ특공대 이희영
     */
    @PostMapping("/update/{id}")
    public String updateGroup(@ModelAttribute CreateForm createForm, MultipartFile settingImage, @PathVariable int id) throws IOException {
        String siGunGuName = createForm.getSiGunGuName();
        SiGunGu siGunGu = siGunGuService.findById(siGunGuName);

        String selectedSubject = createForm.getSubject();
        String subject = subjectChange(selectedSubject);

        StudyGroup studyGroup = StudyGroup.builder()
                .id(id)
                .name(createForm.getName())
                .totalCount(createForm.getTotalCount())
                .subject(subject)
                .siGunGuId(siGunGu.getId())
                .build();

        if (!settingImage.isEmpty()) {
            // 이미지 폴더에 저장
            // 업로드 이미지 확장자 가져오기
            String imageExtension = StringUtils.getFilenameExtension(settingImage.getOriginalFilename());
            // 업로드 한 이미지 다운로드 받을 위치 설정
            StringBuilder imageDir = new StringBuilder();
            imageDir.append(imageLocation).append(id).append(".").append(imageExtension);

            File uploadDir = new File(imageDir.toString());
            // 폴더 없으면 생성
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            settingImage.transferTo(uploadDir);

            StringBuilder imagePathDB = new StringBuilder();
            imagePathDB.append(imageDBPath).append(id).append(".").append(imageExtension);
            studyGroup.setImagePath(imagePathDB.toString());
        } else {
            studyGroup.setImagePath("");
        }

        studyGroupService.editStudy(studyGroup);

        return "redirect:/group/{id}";
    }

    /**
     * 스터디 그룹 삭제
     *
     * @param id 스터디 그룹 아이디
     * @return 내 스터디 그룹 리스트 화면
     * @author VJ특공대 이희영
     */
    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable int id) {
        studyGroupService.removeStudy(id);

        return "redirect:/group/mygroup";
    }

    /**
     * 스터디 그룹 가입
     *
     * @param id          스터디 그룹 아이디
     * @param loginMember 로그인 멤버
     * @return 스터디 그룹 상세 화면
     * @author VJ특공대 이희영
     */
    @ResponseBody
    @PostMapping("/join/{id}")
    public String joinGroup(@PathVariable int id, @SessionAttribute Member loginMember, @RequestBody String contact) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ContactInfo contactInfo = objectMapper.readValue(contact, ContactInfo.class);
        Member member = memberService.getMember(loginMember.getId());

        if (member.getGender() == null || member.getBirthday() == null) {
            member.setGender(contactInfo.getGender());
            member.setBirthday(contactInfo.getBirthday());
            memberService.editMember(member);
        }

        GroupContact groupContact = studyGroupService.contactStudy(loginMember.getId(), id);

        if (groupContact == null) {
            return "contact-success";
        } else {
            return "contact-failure";
        }
    }

    /**
     * 스터디 그룹 생성 및 수정 기능에서 사용할 스터디 그룹 주제 변환 기능
     *
     * @param selectedSubject Form에서 선택된 스터디 그룹 주제 옵션
     * @return DB에 입력될 스터디 그룹 주제
     * @author VJ특공대 이희영
     */
    private String subjectChange(String selectedSubject) {
        selectedSubject = switch (selectedSubject) {
            case "stock" -> "주식 / 코인";
            case "econo" -> "경제 / 부동산";
            case "emplo" -> "취업";
            case "inter" -> "면접";
            case "essay" -> "논술";
            case "langu" -> "언어";
            case "it" -> "IT";
            case "certi" -> "자격증";
            case "study" -> "공부";
            default -> "기타";
        };

        return selectedSubject;
    }

    /**
     * 게시글 등록
     *
     * @param createForm 게시글 등록 폼 객체
     * @param request    HttpServletRequest 객체
     * @param model      model 인터페이스
     * @return 스터디 그룹 화면
     * @author VJ특공대 이한솔
     */
    @PostMapping("/{id}/article")
    public String create(@ModelAttribute ArticleCreateForm createForm, @PathVariable int id, MultipartFile imagePath, HttpServletRequest request, Model model) throws IOException {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            int memberId = loginMember.getId(); // Member 객체에서 member_id를 가져옵니다.
            createForm.setStudyGroupId(id);
            log.info(" 담으려는 아티클 폼 {}", createForm);
            log.info("이미지 패스ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ{}", imagePath);

            Article article = Article.builder()
                    .title(createForm.getTitle())
                    .content(createForm.getContent())
                    .memberId(memberId)
                    .studyGroupId(createForm.getStudyGroupId())
                    .build();
            log.info("아티클 객체에 넣어둔 정보 확인 ======{}", article);
            if (imagePath == null) {
                article.setImagePath(null);
                log.info("이미지패스 null 일때 {}", article);
            } else if (!imagePath.isEmpty()) {
                // 이미지 폴더에 저장
                // 업로드 이미지 확장자 가져오기
                String imageExtension = StringUtils.getFilenameExtension(imagePath.getOriginalFilename());
                // 업로드 한 이미지 다운로드 받을 위치 설정
                StringBuilder imageDir = new StringBuilder();
                imageDir.append(articleImageLocation).append(loginMember.getId()).append(".").append(imageExtension);
                File uploadDir = new File(imageDir.toString());
                // 폴더 없으면 생성
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                imagePath.transferTo(uploadDir);

                StringBuilder imagePathDB = new StringBuilder();
                imagePathDB.append(articleImageDBPath).append(loginMember.getId()).append(".").append(imageExtension);
                article.setImagePath(imagePathDB.toString());
            }

            log.info("-----------create 전 아티클 {}", article);
            articleService.create(article);
            log.info("생성 후 아티클 {}", article);

        }
        return "redirect:/group/{id}";
    }

    /**
     * 게시글 삭제
     *
     * @param articleId 게시글 아이디
     * @param article   게시글
     * @param model     model 인터페이스
     * @return 스터디 그룹 화면
     * @author VJ특공대 이한솔
     */
    @PostMapping("/{id}/{articleId}/article/delete")
    public String delete(@PathVariable("articleId") int articleId, @ModelAttribute Article article, HttpServletRequest request,
                         HttpServletResponse response, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        log.info("asdasdads{}",articleId);
        Article targetArticle = articleService.findById(articleId);
        log.info("444444444444444444444444444444: {}", targetArticle);
        int articleMemberId = targetArticle.getMemberId();
        log.info("333 : {}", articleMemberId);
        log.info("2222 : {}", loginMember.getId());

        if (loginMember.getId() == articleMemberId) {
            articleService.delete(articleId);
        } else {

            try {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter w = response.getWriter();
                w.write("<script>alert('글쓴이만 삭제할 수 있습니다..');location.href='/group';</script>");
                w.flush();
                w.close();
            } catch (Exception e) {
                throw new RuntimeException("오류 메세지");
            }
        }

        return "redirect:/group/{id}";
    }

    /**
     * 게시글 수정
     *
     * @param
     * @param model model 인터페이스
     * @param article 아티클 객체
     * @return 스터디 그룹 화면
     * @author VJ특공대 이한솔
     */
    @PostMapping("/{id}/{articleId}/article/update")
    public String update(@ModelAttribute Article article, HttpServletRequest request, MultipartFile imagePath,
                         HttpServletResponse response, Model model) throws IOException {
        log.info("시작 ===========================1");
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Article targetArticle = articleService.findById(article.getId());
        log.info("{}",targetArticle);
        int articleMemberId = targetArticle.getMemberId();
        if (loginMember.getId() == articleMemberId) {
            if (imagePath == null) {

                model.addAttribute("targetArticle", article);
                articleService.update(article);
                model.addAttribute("article", article);

            } else if (!imagePath.isEmpty()) {
                // 이미지 폴더에 저장
                // 업로드 이미지 확장자 가져오기
                String imageExtension = StringUtils.getFilenameExtension(imagePath.getOriginalFilename());
                // 업로드 한 이미지 다운로드 받을 위치 설정
                StringBuilder imageDir = new StringBuilder();
                imageDir.append(articleImageLocation).append(loginMember.getId()).append(".").append(imageExtension);
                File uploadDir = new File(imageDir.toString());
                // 폴더 없으면 생성
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                imagePath.transferTo(uploadDir);

                StringBuilder imagePathDB = new StringBuilder();
                imagePathDB.append(articleImageDBPath).append(loginMember.getId()).append(".").append(imageExtension);
                article.setImagePath(imagePathDB.toString());

                model.addAttribute("targetArticle", article);
                articleService.update(article);
                model.addAttribute("article", article);
            }
        } else {

            try {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter w = response.getWriter();
                w.write("<script>alert('글쓴이만 수정할 수 있습니다..');location.href='/group';</script>");
                w.flush();
                w.close();
            } catch (Exception e) {
                throw new RuntimeException("오류 메세지");
            }
        }

        return "redirect:/group/{id}";
    }

    /**
     * 게시글 댓글 등록
     *
     * @param articleComment 게시글 댓글
     * @param articleId      게시글 아이디
     * @param request        HttpServletRequest 객체
     * @param model          model 인터페이스
     * @return 스터디 그룹 화면
     * @author VJ특공대 이한솔
     */
    @PostMapping("/{id}/{articleId}/commentCreate")
    public String commentCreate(@ModelAttribute ArticleComment articleComment, @PathVariable int articleId, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            int memberId = loginMember.getId();
            articleComment.setMemberId(memberId);
            articleComment.setArticleId(articleId);
            articleCommentService.create(articleComment);
        }

        return "redirect:/group/{id}";
    }

    /**
     * 게시글 댓글 삭제
     *
     * @param id             댓글 아이디
     * @param articleComment 게시글 댓글
     * @param model          model 인터페이스
     * @return 스터디 그룹 화면
     * @author VJ특공대 이한솔
     */
    @PostMapping("/{id}/commentDelete")
    public String delete(@PathParam("commentId") int id, @ModelAttribute ArticleComment articleComment, Model model) {
        ArticleComment targetComment = articleCommentService.findById(id);
        model.addAttribute("articleComment", targetComment);
        articleCommentService.delete(id);

        return "redirect:/group/{id}";
    }
}