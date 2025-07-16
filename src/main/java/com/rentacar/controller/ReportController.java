package com.rentacar.controller;

import com.rentacar.model.Reservation;
import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String showReportsPage(Model model) {
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("oneMonthAgo", currentDate.minusMonths(1));
        return "admin/reports/index";
    }

    @GetMapping("/revenue")
    public String generateRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all confirmed/completed reservations in date range
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> (r.getStatus() == Reservation.ReservationStatus.CONFIRMED || 
                             r.getStatus() == Reservation.ReservationStatus.COMPLETED))
                .filter(r -> !r.getStartDate().isAfter(endDateTime) && !r.getEndDate().isBefore(startDateTime))
                .collect(Collectors.toList());
        
        // Calculate total revenue
        BigDecimal totalRevenue = reservations.stream()
                .map(Reservation::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Revenue by vehicle type
        Map<Vehicle.VehicleType, BigDecimal> revenueByType = new HashMap<>();
        for (Reservation reservation : reservations) {
            if (reservation.getVehicle() != null && reservation.getVehicle().getType() != null) {
                Vehicle.VehicleType type = reservation.getVehicle().getType();
                BigDecimal cost = reservation.getTotalCost() != null ? reservation.getTotalCost() : BigDecimal.ZERO;
                BigDecimal currentRevenue = revenueByType.getOrDefault(type, BigDecimal.ZERO);
                revenueByType.put(type, currentRevenue.add(cost));
            }
        }
        
        // Reservation count by vehicle type - safer implementation
        Map<Vehicle.VehicleType, Long> countByType = reservations.stream()
                .filter(r -> r.getVehicle() != null && r.getVehicle().getType() != null)
                .collect(Collectors.groupingBy(r -> r.getVehicle().getType(), Collectors.counting()));
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reservations", reservations);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("revenueByType", revenueByType);
        model.addAttribute("countByType", countByType);
        model.addAttribute("reservationCount", reservations.size());
        
        return "admin/reports/revenue";
    }

    @GetMapping("/fleet")
    public String generateFleetReport(Model model) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        
        // Vehicle count by type
        Map<Vehicle.VehicleType, Long> countByType = vehicles.stream()
                .collect(Collectors.groupingBy(Vehicle::getType, Collectors.counting()));
        
        // Average rating by type
        Map<Vehicle.VehicleType, Double> avgRatingByType = vehicles.stream()
                .filter(v -> v.getRating() != null)
                .collect(Collectors.groupingBy(
                    Vehicle::getType,
                    Collectors.averagingDouble(v -> v.getRating() != null ? v.getRating() : 0)
                ));
        
        // Vehicle availability percentage
        long availableCount = vehicles.stream().filter(Vehicle::isAvailable).count();
        double availabilityPercentage = vehicles.isEmpty() ? 0 : (double) availableCount / vehicles.size() * 100;
        
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicleCount", vehicles.size());
        model.addAttribute("countByType", countByType);
        model.addAttribute("avgRatingByType", avgRatingByType);
        model.addAttribute("availabilityPercentage", availabilityPercentage);
        
        return "admin/reports/fleet";
    }

    @GetMapping("/users")
    public String generateUserReport(Model model) {
        List<User> users = userRepository.findAll();
        
        // Users by role
        Map<User.Role, Long> usersByRole = users.stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
        
        // Active vs inactive users
        long activeUsers = users.stream().filter(User::isEnabled).count();
        long inactiveUsers = users.size() - activeUsers;
        
        // Top users by reservation count
        Map<User, Long> reservationCountByUser = users.stream()
                .collect(Collectors.toMap(
                    user -> user,
                    user -> (long) user.getReservations().size()
                ));
        
        List<Map.Entry<User, Long>> topUsers = reservationCountByUser.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        model.addAttribute("users", users);
        model.addAttribute("userCount", users.size());
        model.addAttribute("usersByRole", usersByRole);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("inactiveUsers", inactiveUsers);
        model.addAttribute("topUsers", topUsers);
        
        return "admin/reports/users";
    }
}
