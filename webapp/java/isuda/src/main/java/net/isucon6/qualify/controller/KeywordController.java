package net.isucon6.qualify.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.isucon6.qualify.domain.Entry;
import net.isucon6.qualify.domain.Keyword;
import net.isucon6.qualify.dto.EntryDto;
import net.isucon6.qualify.form.KeywordForm;
import net.isucon6.qualify.service.EntryService;
import net.isucon6.qualify.service.KeywordService;
import net.isucon6.qualify.service.SpamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class KeywordController {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private SpamService spamService;
    @Autowired
    private EntryService entryService;

    @RequestMapping(value = "/keyword", method = RequestMethod.POST)
    public ModelAndView create(
            @Valid @ModelAttribute KeywordForm form,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()
                || spamService.isSpam(form.getKeyword())
                || spamService.isSpam(form.getDescription())) {
            ModelAndView mav = new ModelAndView();
            mav.setStatus(HttpStatus.BAD_REQUEST);
            mav.setViewName("400");
            return mav;
        }

        keywordService.insert(
                new Keyword(
                        (Long) session.getAttribute("user_id"),
                        form.getKeyword(),
                        form.getDescription()
                )
        );

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/keyword/{keyword}")
    public ModelAndView show(@PathVariable("keyword") String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            ModelAndView mav = new ModelAndView();
            mav.setStatus(HttpStatus.BAD_REQUEST);
            mav.setViewName("400");
        }

        EntryDto entryDto = entryService.findByKeyword(keyword);

        if (entryDto == null) {
            return new ModelAndView("404", new HashMap<>(), HttpStatus.NOT_FOUND);
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("entry", entryDto);
        mav.setViewName("keyword");
        return mav;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView delete() {
        return null;
    }
}
