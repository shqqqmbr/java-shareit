package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    //    Убрал лишний метод, jpql запрос сделал универсальным
//            для поиска по bookerId и itemOwnerId
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "OR b.booker.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerIdOrderByStartDesc(Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Integer ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndEndBefore(Integer ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStartAfter(Integer ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByitemOwnerIdAndStatus(Integer ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.end < :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Optional<Booking> findLastCompletedBooking(@Param("itemId") int itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start > :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC " +
            "LIMIT 1")
    Optional<Booking> findNextActiveBooking(@Param("itemId") int itemId, LocalDateTime now);

    boolean existsByBookerIdAndItemIdAndEndBefore(int bookerId, int itemId, LocalDateTime endTime);
}
