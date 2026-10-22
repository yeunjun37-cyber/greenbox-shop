package com.greenbox.shop.web;

import com.greenbox.shop.domain.plant.MyPlant;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.MyPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/myplants")
@RequiredArgsConstructor
public class MyPlantController {

    private final MyPlantService myPlantService;

    /** 주간 물주기 일정 표시용 */
    public record WeekDay(LocalDate date, String dow, List<String> names) {
    }

    @GetMapping
    public String list(@AuthenticationPrincipal LoginMember login, Model model) {
        List<MyPlant> plants = myPlantService.list(login.getId());
        LocalDate today = LocalDate.now();
        List<WeekDay> week = IntStream.range(0, 7).mapToObj(today::plusDays)
                .map(d -> new WeekDay(d, d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                        plants.stream().filter(p -> d.equals(p.getNextWaterAt()) || (d.equals(today) && p.isDue()))
                                .map(MyPlant::getNickname).toList()))
                .toList();
        model.addAttribute("plants", plants);
        model.addAttribute("week", week);
        model.addAttribute("purchased", myPlantService.purchasedPlants(login.getId()));
        model.addAttribute("today", today);
        model.addAttribute("menu", "plants");
        return "myplant/list";
    }

    /** F-21 내 식물 등록 */
    @PostMapping
    public String register(@RequestParam Long productId, @RequestParam String nickname,
                           @RequestParam(required = false) Integer waterCycle,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastWateredAt,
                           @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        myPlantService.register(login.getId(), productId, nickname, waterCycle, lastWateredAt);
        ra.addFlashAttribute("message", "‘" + nickname + "’을(를) 내 식물로 등록했습니다.");
        return "redirect:/myplants";
    }

    /** F-22 물주기 기록 */
    @PostMapping("/{id}/water")
    public String water(@PathVariable Long id, @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        MyPlant plant = myPlantService.water(login.getId(), id);
        ra.addFlashAttribute("message", plant.getNickname() + "에게 물을 줬어요. 다음 물주기는 "
                + plant.getNextWaterAt().format(DateTimeFormatter.ofPattern("M월 d일")) + "입니다.");
        return "redirect:/myplants";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal LoginMember login) {
        myPlantService.delete(login.getId(), id);
        return "redirect:/myplants";
    }
}
