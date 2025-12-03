/*
Author: Sotirios Ilias
Student ID: it21925
*/

package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.model.OpeningHour;
import gr.hua.dit.studyrooms.core.model.StudySpace;
import gr.hua.dit.studyrooms.core.service.StudySpaceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@Controller
@RequestMapping("/spaces")
public class StudySpaceController {

    private final StudySpaceService service;

    public StudySpaceController(StudySpaceService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("spaces", service.findAll());
        return "spaces/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("space", new StudySpace());
        model.addAttribute("days", DayOfWeek.values());
        return "spaces/create";
    }

    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("space") StudySpace space,
                               BindingResult binding,
                               @RequestParam Map<String,String> params,
                               Model model) {
        try {
            parseOpeningHoursFromParams(space, params);
            if (binding.hasErrors()) {
                model.addAttribute("days", DayOfWeek.values());
                return "spaces/create";
            }
            service.create(space);
            return "redirect:/spaces";
        } catch (Exception ex) {
            binding.reject("globalError", ex.getMessage());
            model.addAttribute("days", DayOfWeek.values());
            return "spaces/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        StudySpace space = service.findById(id).orElseThrow();
        model.addAttribute("space", space);
        model.addAttribute("days", DayOfWeek.values());
        return "spaces/edit";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("space") StudySpace space,
                             BindingResult binding,
                             @RequestParam Map<String,String> params,
                             Model model) {
        try {
            parseOpeningHoursFromParams(space, params);
            if (binding.hasErrors()) {
                model.addAttribute("days", DayOfWeek.values());
                return "spaces/edit";
            }
            service.update(id, space);
            return "redirect:/spaces";
        } catch (Exception ex) {
            binding.reject("globalError", ex.getMessage());
            model.addAttribute("days", DayOfWeek.values());
            return "spaces/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/spaces";
    }

    /* Example parsing convention (simple):
       The form posts repeated fields:
         oh_day_0 = MONDAY
         oh_open_0 = 08:00
         oh_close_0 = 20:00
         oh_day_1 = TUESDAY
         ...
       This method extracts them and sets space.openingHours
    */
    private void parseOpeningHoursFromParams(StudySpace space, Map<String,String> params) {
        // clear existing
        space.setOpeningHours(new HashSet<>());
        // find keys with prefix "oh_day_"
        params.keySet().stream()
                .filter(k -> k.startsWith("oh_day_"))
                .forEach(dayKey -> {
                    String suffix = dayKey.substring("oh_day_".length());
                    String dayVal = params.get(dayKey);
                    String openVal = params.get("oh_open_" + suffix);
                    String closeVal = params.get("oh_close_" + suffix);
                    if (dayVal == null || openVal == null || closeVal == null) return;
                    DayOfWeek dow = DayOfWeek.valueOf(dayVal);
                    LocalTime open = LocalTime.parse(openVal);
                    LocalTime close = LocalTime.parse(closeVal);
                    OpeningHour oh = new OpeningHour(dow, open, close);
                    space.addOpeningHour(oh);
                });
    }
}
