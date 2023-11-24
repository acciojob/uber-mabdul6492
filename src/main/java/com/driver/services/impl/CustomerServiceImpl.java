package com.driver.services.impl;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.model.TripBooking;
import com.driver.model.TripStatus;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Iterable<Driver> driverIterable = driverRepository2.findAll();

		int lowestDriverId = Integer.MAX_VALUE;
		boolean isAvailable = false;
		for(Driver driver: driverIterable){
			if(driver.getCab().getAvailable()){
				isAvailable = true;
				if(driver.getDriverId() < lowestDriverId)
					lowestDriverId = driver.getDriverId();
			}
		}

		if(!isAvailable) throw new Exception("No cab available!");

		TripBooking tripBooking = new TripBooking();

		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);

		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> tripBookingList = customer.getTripBookingList();
		tripBookingList.add(tripBooking);
		customerRepository2.save(customer);

		Driver driver = driverRepository2.findById(lowestDriverId).get();
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);

		tripBooking.setBill(driver.getCab().getPerKmRate()*distanceInKm);
		tripBookingRepository2.save(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBookingOptional = tripBookingRepository2.findById(tripId);
		if(tripBookingOptional.isPresent()){
			TripBooking tripBooking = tripBookingOptional.get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBooking.setBill(0);
			Driver driver = tripBooking.getDriver();
			driver.getCab().setAvailable(true);
			driverRepository2.save(driver);
			tripBookingRepository2.save(tripBooking);
		}
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBookingOptional = tripBookingRepository2.findById(tripId);
		if(tripBookingOptional.isPresent()){
			TripBooking tripBooking = tripBookingOptional.get();
			tripBooking.setStatus(TripStatus.COMPLETED);
			Driver driver = tripBooking.getDriver();
			driver.getCab().setAvailable(true);
			driverRepository2.save(driver);
			tripBookingRepository2.save(tripBooking);
		}
	}
}
