package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

class Security {
    private String name;
    private int amount;

    public Security(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

@Service
public class CommandService {
    private final List<Security> securities = new ArrayList<>();

    public String buySecurity(String security, int amount) {
        Security sec = findSecurity(security);
        if (sec == null) {
            return "1 Unknown security";
        }
        sec.setAmount(sec.getAmount() + amount);
        return "0 Trade successful";
    }

    public String addSecurity(String security) {
        if (findSecurity(security) != null) {
            return "0 Success";
        }
        securities.add(new Security(security, 0));
        return "0 Success";
    }

    public String sellSecurity(String security, int amount) {
        Security sec = findSecurity(security);
        if (sec == null) {
            return "1 Unknown security";
        }
        if (sec.getAmount() < amount) {
            return "2 Not enough positions";
        }
        sec.setAmount(sec.getAmount() - amount);
        return "0 Trade successful";
    }

    public String getSecurities() {
        StringBuilder sb = new StringBuilder();
        sb.append("0 ");
        for (Security sec : securities) {
            sb.append(sec.getName()).append(" ").append(sec.getAmount()).append(" | ");
        }
        if (sb.length() > 3) {
            sb.setLength(sb.length() - 3);
        }
        return sb.toString();
    }

    private Security findSecurity(String security) {
        for (Security sec : securities) {
            if (sec.getName().equals(security)) {
                return sec;
            }
        }
        return null;
    }
}

