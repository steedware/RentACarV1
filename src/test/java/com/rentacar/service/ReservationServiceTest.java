package com.rentacar.service;

import com.rentacar.model.Reservation;
import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    private ReservationService reservationService;

    private Reservation testReservation;
    private User testUser;
    private Vehicle testVehicle;
    private final Long TEST_RESERVATION_ID = 1L;

    @BeforeEach
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);
        
        // Initialize the service with mocks
        reservationService = new ReservationService(reservationRepository, vehicleRepository);
        
        // Initialize test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        
        // Initialize test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Corolla");
        testVehicle.setType(Vehicle.VehicleType.SEDAN);
        testVehicle.setDailyRate(new BigDecimal("100.00"));
        testVehicle.setAvailable(true);
        
        // Initialize test reservation
        testReservation = new Reservation();
        testReservation.setId(TEST_RESERVATION_ID);
        testReservation.setUser(testUser);
        testReservation.setVehicle(testVehicle);
        testReservation.setStartDate(LocalDateTime.now().plusDays(1));
        testReservation.setEndDate(LocalDateTime.now().plusDays(3));
        testReservation.setTotalCost(new BigDecimal("200.00"));
        testReservation.setStatus(Reservation.ReservationStatus.PENDING);
        
        // Use lenient() for all stubbings to avoid "unnecessary stubbing" errors
        // These will be used by different test methods but not all of them
        lenient().when(reservationRepository.findById(TEST_RESERVATION_ID)).thenReturn(Optional.of(testReservation));
        lenient().when(reservationRepository.findAll()).thenReturn(Arrays.asList(testReservation));
        lenient().when(reservationRepository.findByUser(testUser)).thenReturn(Arrays.asList(testReservation));
        lenient().when(reservationRepository.findAllByVehicle(testVehicle)).thenReturn(Arrays.asList(testReservation));
        lenient().when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // Setup for findOverlappingReservations
        lenient().when(reservationRepository.findOverlappingReservations(
                eq(testVehicle), 
                any(LocalDateTime.class), 
                any(LocalDateTime.class),
                any(Reservation.ReservationStatus.class)))
            .thenReturn(new ArrayList<>());
    }
    
    // All test methods remain the same...
    @Test
    void testGetAllReservations() {
        List<Reservation> result = reservationService.getAllReservations();
        
        assertEquals(1, result.size());
        assertEquals(TEST_RESERVATION_ID, result.get(0).getId());
        verify(reservationRepository, times(1)).findAll();
    }
    
    @Test
    void testGetReservationById() {
        Optional<Reservation> result = reservationService.getReservationById(TEST_RESERVATION_ID);
        
        assertTrue(result.isPresent());
        assertEquals(TEST_RESERVATION_ID, result.get().getId());
        verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
    }
    
    @Test
    void testGetReservationsByUser() {
        List<Reservation> result = reservationService.getReservationsByUser(testUser);
        
        assertEquals(1, result.size());
        assertEquals(TEST_RESERVATION_ID, result.get(0).getId());
        verify(reservationRepository, times(1)).findByUser(testUser);
    }
    
    @Test
    void testGetReservationsByVehicle() {
        List<Reservation> result = reservationService.getReservationsByVehicle(testVehicle);
        
        assertEquals(1, result.size());
        assertEquals(TEST_RESERVATION_ID, result.get(0).getId());
        verify(reservationRepository, times(1)).findAllByVehicle(testVehicle);
    }
    
    @Test
    void testCreateReservation_Success() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        
        Reservation result = reservationService.createReservation(testUser, testVehicle, startDate, endDate);
        
        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.PENDING, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testCreateReservation_OverlappingReservations() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        
        List<Reservation> overlappingReservations = Arrays.asList(testReservation);
        when(reservationRepository.findOverlappingReservations(
                eq(testVehicle), 
                any(LocalDateTime.class), 
                any(LocalDateTime.class),
                any(Reservation.ReservationStatus.class)))
            .thenReturn(overlappingReservations);
        
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(testUser, testVehicle, startDate, endDate);
        });
    }
    
    @Test
    void testConfirmReservation() {
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(TEST_RESERVATION_ID);
        confirmedReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        when(reservationRepository.save(any(Reservation.class))).thenReturn(confirmedReservation);
        
        Reservation result = reservationService.confirmReservation(TEST_RESERVATION_ID);
        
        assertEquals(Reservation.ReservationStatus.CONFIRMED, result.getStatus());
        verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testCancelReservation() {
        Reservation canceledReservation = new Reservation();
        canceledReservation.setId(TEST_RESERVATION_ID);
        canceledReservation.setStatus(Reservation.ReservationStatus.CANCELED);
        
        when(reservationRepository.save(any(Reservation.class))).thenReturn(canceledReservation);
        
        Reservation result = reservationService.cancelReservation(TEST_RESERVATION_ID);
        
        assertEquals(Reservation.ReservationStatus.CANCELED, result.getStatus());
        verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testCompleteReservation() {
        Reservation completedReservation = new Reservation();
        completedReservation.setId(TEST_RESERVATION_ID);
        completedReservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        
        when(reservationRepository.save(any(Reservation.class))).thenReturn(completedReservation);
        
        Reservation result = reservationService.completeReservation(TEST_RESERVATION_ID);
        
        assertEquals(Reservation.ReservationStatus.COMPLETED, result.getStatus());
        verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testRateReservation() {
        // Set up vehicle with existing ratings
        Vehicle ratedVehicle = new Vehicle();
        ratedVehicle.setId(1L);
        ratedVehicle.setRatingCount(5);
        ratedVehicle.setRating(4.0);
        
        // Set up reservation with rated vehicle
        Reservation ratedReservation = new Reservation();
        ratedReservation.setId(TEST_RESERVATION_ID);
        ratedReservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        ratedReservation.setVehicle(ratedVehicle);
        
        when(reservationRepository.findById(TEST_RESERVATION_ID)).thenReturn(Optional.of(ratedReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(ratedReservation);
        
        Reservation result = reservationService.rateReservation(TEST_RESERVATION_ID, 5, "Great car!");
        
        assertEquals(5, result.getRating());
        assertEquals("Great car!", result.getFeedback());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
    
    @Test
    void testRateReservation_InvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.rateReservation(TEST_RESERVATION_ID, 6, "Invalid rating!");
        });
    }
}
