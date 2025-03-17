// Configurable API Base URL for Bus Ticket Booking
// const API_BASE_URL = "http://localhost:8080/busticket";
const API_BASE_URL = "https://guvi-k2tt.onrender.com/busticket";
// ----------------------
// Utility Functions
// ----------------------
function getQueryParam(name) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(name);
}

function getDistinct(arr) {
  return [...new Set(arr)];
}

// ----------------------
// Passenger Registration
// ----------------------
if (document.getElementById('registerForm')) {
  document.getElementById('registerForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const registrationData = {
      name: document.getElementById('name').value,
      email: document.getElementById('email').value,
      contactNumber: document.getElementById('contactNumber').value,
      password: document.getElementById('password').value
    };
    fetch(`${API_BASE_URL}/passengers/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(registrationData)
    })
    .then(response => response.json())
    .then(data => {
      document.getElementById('registrationMessage').innerText =
        "Registration successful! Your Passenger ID is: " + data.id;
    })
    .catch(error => {
      console.error('Error during registration:', error);
      document.getElementById('registrationMessage').innerText = "Enterd Mail Already Exist!";
    });
  });
}

// ----------------------
// Passenger Login
// ----------------------
if (document.getElementById('passengerLoginForm')) {
  document.getElementById('passengerLoginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const loginData = {
      email: document.getElementById('loginEmail').value,
      password: document.getElementById('loginPassword').value
    };

    fetch(`${API_BASE_URL}/passengers/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginData)
    })
    .then(response => response.json())
    .then(data => {
      if (data && data.id) {
        // Save the passenger ID in localStorage
        localStorage.setItem('patientId', data.id);
        window.location.href = "passenger_dashboard.html?patientId=" + data.id;
      } else {
        document.getElementById('passengerLoginMessage').innerText = 'Login failed!';
      }
    })
    .catch(error => {
      console.error('Error during login:', error);
      document.getElementById('passengerLoginMessage').innerText = 'Login failed!';
    });
  });
}

// ----------------------
// Bus Search & Suggestions (search_bus.html)
// ----------------------
if (document.getElementById('searchForm')) {
  document.getElementById('searchForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const from = document.getElementById('from').value;
    const to = document.getElementById('to').value;
    window.location.href = "bus_routes.html?from=" + encodeURIComponent(from) + "&to=" + encodeURIComponent(to);
  });
}

function loadBusSuggestions() {
  fetch(`${API_BASE_URL}/buses`)
    .then(response => response.json())
    .then(buses => {
      const fromValues = buses.map(bus => bus.from);
      const toValues = buses.map(bus => bus.to);
      const distinctFrom = getDistinct(fromValues);
      const distinctTo = getDistinct(toValues);
      
      const fromDatalist = document.getElementById('fromSuggestions');
      const toDatalist = document.getElementById('toSuggestions');
      if (fromDatalist) {
        fromDatalist.innerHTML = "";
        distinctFrom.forEach(value => {
          const option = document.createElement('option');
          option.value = value;
          fromDatalist.appendChild(option);
        });
      }
      if (toDatalist) {
        toDatalist.innerHTML = "";
        distinctTo.forEach(value => {
          const option = document.createElement('option');
          option.value = value;
          toDatalist.appendChild(option);
        });
      }
    })
    .catch(error => console.error('Error loading bus suggestions:', error));
}

// ----------------------
// Load Bus Routes (bus_routes.html)
// ----------------------
function loadBusRoutes(from, to) {
  fetch(`${API_BASE_URL}/buses/search?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`)
    .then(response => response.json())
    .then(buses => {
      let html = `<table class="table table-bordered">
                    <thead>
                      <tr>
                        <th>Bus Number</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Departure Time</th>
                        <th>Arrival Time</th>
                        <th>Price</th>
                        <th>Available Seats</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>`;
      buses.forEach(bus => {
        html += `<tr>
                   <td>${bus.busNumber}</td>
                   <td>${bus.from}</td>
                   <td>${bus.to}</td>
                   <td>${bus.departureTime}</td>
                   <td>${bus.arrivalTime}</td>
                   <td>$${bus.price}</td>
                   <td>${bus.availableSeats}</td>
                   <td>
                     <button class="btn btn-sm btn-primary" onclick="bookTicket('${bus.id}')">Book Ticket</button>
                   </td>
                 </tr>`;
      });
      html += `</tbody></table>`;
      document.getElementById('busRoutesTable').innerHTML = html;
    })
    .catch(error => console.error('Error loading bus routes:', error));
}

function bookTicket(busId) {
  window.location.href = "booking_form.html?busId=" + busId;
}

// ----------------------
// Booking Form (booking_form.html)
// ----------------------
if (document.getElementById('bookingForm')) {
  let selectedSeats = [];
  const busId = getQueryParam('busId');
  const passengerId = localStorage.getItem('patientId');
  const seatMapContainer = document.getElementById('seatMap');
  const bookingMessage = document.getElementById('bookingMessage');

  fetch(`${API_BASE_URL}/buses/${busId}`)
    .then(response => response.json())
    .then(bus => {
      if (!bus || !bus.seats) {
        bookingMessage.innerText = "No seat data found for this bus.";
        return;
      }
      renderSeatMap(bus.seats);
    })
    .catch(error => {
      console.error("Error fetching bus seat data:", error);
      bookingMessage.innerText = "Error loading seat map.";
    });

  function renderSeatMap(seats) {
    seatMapContainer.innerHTML = "";
    seats.forEach(seat => {
      const seatDiv = document.createElement('div');
      seatDiv.classList.add('seat', seat.status);
      seatDiv.textContent = seat.seatId;
      seatDiv.dataset.seatId = seat.seatId;
      seatDiv.addEventListener('click', function() {
        if (seat.status === 'unavailable') return;
        const maxTickets = parseInt(document.getElementById('ticketCount').value, 10);
        if (seatDiv.classList.contains('selected')) {
          seatDiv.classList.remove('selected');
          selectedSeats = selectedSeats.filter(s => s !== seat.seatId);
        } else {
          if (selectedSeats.length < maxTickets) {
            seatDiv.classList.add('selected');
            selectedSeats.push(seat.seatId);
          } else {
            alert("You can only select " + maxTickets + " seats.");
          }
        }
        console.log("Selected Seats:", selectedSeats);
      });
      seatMapContainer.appendChild(seatDiv);
    });
  }

  document.getElementById('bookingForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (selectedSeats.length === 0) {
      bookingMessage.innerText = "Please select at least one seat!";
      return;
    }
    const boardingPoint = document.getElementById('boardingPoint').value;
    const dropPoint = document.getElementById('dropPoint').value;
    if (!boardingPoint || !dropPoint) {
      bookingMessage.innerText = "Please enter boarding and drop points!";
      return;
    }
    const bookingData = {
      passengerId: passengerId,
      busId: busId,
      seatNumbers: selectedSeats,
      bookingDate: new Date().toISOString(),
      status: "PENDING", // Passenger details and payment will be added later
      boardingPoint: boardingPoint,
      dropPoint: dropPoint
    };
    localStorage.setItem("bookingData", JSON.stringify(bookingData));
    window.location.href = "passenger_details.html";
  });
}

// ----------------------
// Booking History (booking_history.html)
// ----------------------
function loadBookingHistory(patientId) {
  fetch(`${API_BASE_URL}/bookings/passenger/${patientId}`)
    .then(response => response.json())
    .then(bookings => {
      let html = `<table class="table table-bordered">
                    <thead>
                      <tr>
                        <th>Booking ID</th>
                        <th>Passenger ID</th>
                        <th>Booking Date</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>`;
      bookings.forEach(booking => {
        html += `<tr>
                   <td>${booking.id}</td>
                   <td>${booking.passengerId}</td>
                   <td>${booking.bookingDate}</td>
                   <td>
                     <button class="btn btn-sm btn-primary" onclick='viewBooking("${booking.id}")'>View Booking</button>
                     <button class="btn btn-sm btn-secondary" onclick='downloadBooking("${booking.id}")'>Download Booking</button>
                   </td>
                 </tr>`;
      });
      html += `</tbody></table>`;
      document.getElementById('bookingHistoryTable').innerHTML = html;
    })
    .catch(error => console.error('Error loading booking history:', error));
}

function viewBooking(bookingId) {
  window.location.href = "view_booking.html?bookingId=" + bookingId;
}

function downloadBooking(bookingId) {
  fetch(`${API_BASE_URL}/bookings/${bookingId}`)
    .then(response => response.json())
    .then(booking => {
      const { jsPDF } = window.jspdf;
      const doc = new jsPDF();
      doc.setFontSize(16);
      doc.text("Booking Ticket", 20, 20);
      doc.setFontSize(12);
      let y = 30;
      doc.text(`Booking ID: ${booking.id}`, 20, y);
      y += 10;
      doc.text(`Passenger ID: ${booking.passengerId}`, 20, y);
      y += 10;
      doc.text(`Bus ID: ${booking.busId}`, 20, y);
      y += 10;
      doc.text(`Bus Number: ${booking.busNumber || "N/A"}`, 20, y);
      y += 10;
      doc.text(`Departure Time: ${booking.departureTime || "N/A"}`, 20, y);
      y += 10;
      doc.text(`Arrival Time: ${booking.arrivalTime || "N/A"}`, 20, y);
      y += 10;
      doc.text(`Seat Numbers: ${booking.seatNumbers.join(", ")}`, 20, y);
      y += 10;
      doc.text(`Boarding Point: ${booking.boardingPoint}`, 20, y);
      y += 10;
      doc.text(`Drop Point: ${booking.dropPoint}`, 20, y);
      y += 10;
      doc.text(`Booking Date: ${booking.bookingDate}`, 20, y);
      if (booking.passengerDetails && booking.passengerDetails.length > 0) {
        y += 10;
        doc.text("Passenger Details:", 20, y);
        booking.passengerDetails.forEach((pd, index) => {
          y += 10;
          doc.text(`Passenger ${index + 1}: ${pd.name}, Age: ${pd.age}, Gender: ${pd.gender}`, 20, y);
        });
      }
      doc.save("booking_ticket.pdf");
    })
    .catch(error => console.error('Error downloading booking:', error));
}

// ----------------------
// Logout and Back Functions
// ----------------------
function logoutPassenger() {
  localStorage.removeItem('patientId');
  window.location.href = "index.html";
}

function logoutDoctor() {
  localStorage.removeItem('doctorId');
  window.location.href = "index.html";
}

function back() {
  window.history.back();
}
