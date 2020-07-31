package eve.tools.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eve.tools.esi.Api;
import eve.tools.esi.model.character.PublicInfo;
import eve.tools.esi.model.contracts.Contract;
import eve.tools.esi.model.universe.UniverseName;
import eve.tools.service.DataService;
import eve.tools.service.UserService;

@Controller
@Secured("ROLE_EVE_CORP_CONTRACTS")
public class CorporationCourierContractsController {

    @Autowired
    private Api api;

    @Autowired
    private UserService userService;

    @Autowired
    private DataService dataService;

    private List<Contract> outstanding;

    private List<Contract> other;

    private List<Contract> filtered; // outstanding + lastDay

    private Boolean showBoth;

    @RequestMapping("corp-courier-contracts")
    String data(
            Model model,
            @RequestParam(value = "more", required = false) String more
    ) {
        showBoth = more != null && more.equals("1");

        Long characterId = Long.valueOf(userService.getAuthenticatedUser().getUsername());
        PublicInfo publicInfo = api.characterPublicInfo(characterId);

        if (publicInfo == null && api.getClient().getLastError() != null) {
            model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());

        } else if (publicInfo != null) {
            List<Contract> contracts = api.corporationContracts(publicInfo.getCorporation_id());
            if (contracts != null) {
                filter(contracts);
                if (showBoth) {
                    model.addAttribute("contractGroup", new List[] { outstanding, other });
                } else {
                    model.addAttribute("contractGroup", new List[] { outstanding });
                }
                model.addAttribute("chars", characters(filtered));
                model.addAttribute("corps", corporations(filtered));
                model.addAttribute("loc", structures(filtered));
                model.addAttribute("showBoth", showBoth);
            } else if (api.getClient().getLastError() != null) {
                model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());
            }
        }

        return "corp-courier-contracts";
    }

    private void filter(List<Contract> contracts) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int oneDayInMillis = 1000 * 60 * 60 * 24;

        outstanding = new ArrayList<>();
        other = new ArrayList<>();
        filtered = new ArrayList<>();

        for (Contract contract : contracts) {
            if (! contract.getType().equals("courier")) {
                continue;
            }

            if (contract.getStatus().equals("outstanding") &&
                    contract.getDate_expired().getTimeInMillis() >= now.getTimeInMillis() ) {
                outstanding.add(contract);
                filtered.add(contract);
                continue;
            }

            if (showBoth && contract.getStatus().equals("in_progress")) {
                other.add(contract);
                filtered.add(contract);
                continue;
            }

            // finished, deleted, failed, rejected, outstanding + expired
            if (showBoth && contract.getDate_completed() != null &&
                    now.getTimeInMillis() - contract.getDate_completed().getTimeInMillis() < oneDayInMillis * 2) {
                other.add(contract);
                filtered.add(contract);
                continue;
            }
            if (showBoth && contract.getDate_completed() == null &&
                    now.getTimeInMillis() - contract.getDate_issued().getTimeInMillis() < oneDayInMillis * 2) {
                other.add(contract);
                filtered.add(contract);
                //continue;
            }
        }
    }

    private Map<Long, String> characters(List<Contract> contracts) {
        Map<Long, String> chars = new HashMap<>();

        List<Long> charIds = new ArrayList<>();
        for (Contract contract : contracts) {
            if (!charIds.contains(contract.getIssuer_id()) && contract.getIssuer_id() > 0) {
                charIds.add(contract.getIssuer_id());
            }
            if (!charIds.contains(contract.getAcceptor_id()) && contract.getAcceptor_id() > 0) {
                charIds.add(contract.getAcceptor_id());
            }
        }
        if (charIds.size() == 0) {
            return chars;
        }

        List<UniverseName> charNames = api.universeNames(charIds);
        if (charNames == null) {
            return chars;
        }

        for (UniverseName charName : charNames) {
            chars.put(charName.getId(), charName.getName());
        }

        return chars;
    }

    private Map<Long, String> corporations(List<Contract> contracts) {
        Map<Long, String> corps = new HashMap<>();

        List<Long> corpIds = new ArrayList<>();
        for (Contract contract : contracts) {
            if (corpIds.contains(contract.getIssuer_corporation_id()) || contract.getIssuer_corporation_id() <= 0) {
                continue;
            }
            corpIds.add(contract.getIssuer_corporation_id());
        }
        if (corpIds.size() == 0) {
            return corps;
        }

        List<UniverseName> corpNames = api.universeNames(corpIds);
        if (corpNames == null) {
            return corps;
        }

        for (UniverseName corpName : corpNames) {
            corps.put(corpName.getId(), corpName.getName());
        }

        return corps;
    }

    private Map<Long, String> structures(List<Contract> contracts) {
        List<Long> structIds = new ArrayList<>();
        for (Contract contract : contracts) {
            if (! structIds.contains(contract.getStart_location_id())) {
                structIds.add(contract.getStart_location_id());
            }
            if (!structIds.contains(contract.getEnd_location_id())) {
                structIds.add(contract.getEnd_location_id());
            }
        }
        if (structIds.size() == 0) {
            return null;
        }

        return dataService.structureNames(structIds);
    }
}
