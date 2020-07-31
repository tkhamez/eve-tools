package eve.tools;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

	private static final String PATH = "/error";

	@Autowired
	ErrorAttributes errorAttributes;

	@RequestMapping(value = PATH)
	public String error(Model model) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		Map<String, Object> errorAttr = errorAttributes.getErrorAttributes(attr, false);

		model.addAttribute("status", errorAttr.get("status"));
		model.addAttribute("error", errorAttr.get("error"));
		//model.addAttribute("message", errorAttr.get("message"));
		//model.addAttribute("path", errorAttr.get("path"));
		//model.addAttribute("timestamp", errorAttr.get("timestamp"));

		return "error";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
