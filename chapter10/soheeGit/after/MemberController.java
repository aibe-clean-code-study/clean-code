package org.example.aquareserve.controller;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.aquareserve.model.dto.MemberDTO;
import org.example.aquareserve.model.dto.ReservationDTO;
import org.example.aquareserve.model.repository.MemberRepository;
import org.example.aquareserve.model.repository.ReservationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MemberController {
    final private MemberRepository memberRepository;
    final private ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public MemberController(MemberRepository memberRepository, ReservationRepository reservationRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    String index(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        MemberDTO member = (MemberDTO) session.getAttribute("member");

        // 세션에 member가 존재하면 해당 정보를 반환
        if (member != null) {
            request.setAttribute("member", member);
        }
        return "index";
    }

    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/map")
    public String map(HttpServletRequest request) {
        String googleMapKey = dotenv.get("GOOGLE_MAP_KEY");
        request.setAttribute("googleMapKey", googleMapKey);
        return "map";
    }
    @GetMapping("/reservation")
    public String reservation(@RequestParam(name = "placeName", required = false) String placeName, Model model) {
        if (placeName == null || placeName.isEmpty()) {
            placeName = "알 수 없는 장소";
        }
        model.addAttribute("placeName", placeName);
        return "reservation";
    }

    // JSON으로 받을 때 사용
    @PostMapping(value = "/member", consumes = "application/json")
    @ResponseBody
    String addMemberFromJson(@RequestBody MemberDTO memberDTO) throws Exception {
        String hashedPassword = passwordEncoder.encode(memberDTO.password());
        memberRepository.save(new MemberDTO(
                UUID.randomUUID().toString(),
                memberDTO.id(),
                memberDTO.name(),
                memberDTO.email(),
                hashedPassword,
                memberDTO.phoneNumber(),
                memberDTO.certifications()
        ));
        return "{\"success\":true,\"message\":\"회원가입이 완료되었습니다.\"}";
    }

    @PostMapping(value = "/login")
    String login(@RequestBody MemberDTO memberDTO, HttpServletRequest request) throws Exception {
        Optional<MemberDTO> member = memberRepository.findByIDPassword(
                memberDTO.id(),
                memberDTO.password()
        );
        if (member.isEmpty()) {
            return "redirect:/login";
        }
        HttpSession session = request.getSession();
        session.setAttribute("member", member.get());

        return "redirect:/";
    }

    @PostMapping(value = "/reservate")
    String addReservation(@RequestBody ReservationDTO reservationDTO) throws Exception {
        reservationRepository.save(new ReservationDTO(
                UUID.randomUUID().toString(),
                reservationDTO.reservationPlace(),
                reservationDTO.memberID(),
                reservationDTO.timeslotID(),
                reservationDTO.equipmentID(),
                reservationDTO.lessonID(),
                reservationDTO.reservationDate()
        ));
        return "redirect:/";
    }
}
