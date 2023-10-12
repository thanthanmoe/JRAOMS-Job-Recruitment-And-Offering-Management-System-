let originalData = [];
let rowsPerPage = 5; // Default value
let currentPage = 1;

function initializeTable() {
  const tableRows = document.querySelectorAll('#datatable tbody tr');
  tableRows.forEach(row => {
    const rowData = {};
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      const columnHeader = document.querySelector(`#datatable thead th:nth-child(${index + 1})`).textContent;
      rowData[columnHeader] = cell.textContent;
    });
    originalData.push(rowData);
  });
}

function generateTable(dataArray, page = 1) {
  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedData = dataArray.slice(startIndex, endIndex);

  const tableBody = document.querySelector('#datatable tbody');

  // Clear existing rows (if any)
  tableBody.innerHTML = '';

  // Generate table rows and cells dynamically from the paginated data
  paginatedData.forEach((rowData, index) => {
    const row = document.createElement('tr');

    for (const key in rowData) {
      const cell = document.createElement('td');
      cell.textContent = rowData[key];
      row.appendChild(cell);
    }

    // Add the "Edit" cell with an edit button
    const editCell = document.createElement('td');

    if (rowData['Role'] !== 'ADMIN') {
      const editButton = document.createElement('button');

      // Add CSS classes to the button for styling
      editButton.classList.add("btn", "btn-primary", "rounded"); // You can customize the classes here

      editButton.textContent = 'Edit';
      editButton.addEventListener('click', () => editUser(rowData['ID']));

      editCell.appendChild(editButton);
    }

    row.appendChild(editCell);
    tableBody.appendChild(row);


  });
}

function editUser(userId) {
  // Navigate to the edit page with the user ID
  const editUrl = `http://localhost:8080/system/usereditinfo/${userId}`;
  window.location.href = editUrl;
}


function searchTable(searchText) {
  const filteredData = originalData.filter(rowData =>
      Object.values(rowData).some(value =>
          value.toString().toLowerCase().includes(searchText.toLowerCase())
      )
  );

  generateTable(filteredData);
  updatePagination(filteredData.length);
}

let sortedData = null;
let sortOrder = 1;
let sortedColumn = null;

function sortTable(column, isNumeric = false) {
  if (sortedData === null || sortedColumn !== column) {
    sortOrder = 1;
    sortedData = originalData.slice();
  }

  sortedData.sort((a, b) => {
    const valA = a[column];
    const valB = b[column];

    if (column === "ID") {
      // Handle numerical sorting for the ID column
      return (parseInt(valA, 10) - parseInt(valB, 10)) * sortOrder;
    }

    if (isNumeric) {
      // Handle numerical sorting for other numeric columns
      return (parseFloat(valA) - parseFloat(valB)) * sortOrder;
    }

    return valA.localeCompare(valB) * sortOrder;
  });

  sortOrder *= -1;
  sortedColumn = column;

  generateTable(sortedData);
  updatePagination(sortedData.length);

  // Clear previous sorting classes from all headers
  const sortableHeaders = document.querySelectorAll('#datatable th.sortable');
  sortableHeaders.forEach(header => {
    header.classList.remove('sorted-asc', 'sorted-desc');
  });

  // Add the appropriate sorting class to the sorted header
  const sortedHeader = document.querySelector(`#datatable th[data-column="${column}"]`);
  sortedHeader.classList.add(sortOrder === 1 ? 'sorted-asc' : 'sorted-desc');
}

function updatePagination(totalItems) {
  const paginationContainer = document.querySelector('#pagination');
  const totalPages = Math.ceil(totalItems / rowsPerPage);

  if (totalPages <= 1) {
    paginationContainer.innerHTML = '';
    return;
  }

  let paginationHTML = '';

  for (let page = 1; page <= totalPages; page++) {
    paginationHTML += `<span class="pagination-link${page === currentPage ? ' active' : ''}" onclick="changePage(${page})">${page}</span>`;
  }

  paginationContainer.innerHTML = paginationHTML;
}

function changeRowsPerPage() {
  rowsPerPage = parseInt(document.getElementById('rows-per-page').value);
  changePage(currentPage);
}

function previousPage() {
  if (currentPage > 1) {
    currentPage--;
    changePage(currentPage);
  }
}

function nextPage() {
  const totalPages = Math.ceil((sortedData || originalData).length / rowsPerPage);

  if (currentPage < totalPages) {
    currentPage++;
    changePage(currentPage);
  }
}

function changePage(page) {
  generateTable(sortedData || originalData, page);

  // Update active class for pagination links
  const paginationLinks = document.querySelectorAll('.pagination-link');
  paginationLinks.forEach(link => {
    link.classList.remove('active');
  });

  const activeLink = document.querySelector(`.pagination-link:nth-child(${page})`);
  activeLink.classList.add('active');
}

// JavaScript function to toggle user status and update it on the server
function toggleStatus(rowIndex) {
  const dataRow = sortedData || originalData;
  if (rowIndex < 0 || rowIndex >= dataRow.length) {
    return;
  }

  const rowData = dataRow[rowIndex];
  rowData['Status'] = rowData['Status'] === 'Active' ? 'Inactive' : 'Active';
  const isStatusActive = rowData['Status'] === 'Active';

  $.ajax({
    url: '/manageusers', // Replace 'your_url_here' with the actual URL to send the data to
    method: 'POST',
    contentType: 'application/json', // Set the content type to JSON
    data: JSON.stringify(rowData), // Send the entire rowData object as JSON
    success: function(response) {
      console.log('Send success: ' + JSON.stringify(rowData));
      // Handle the successful response from the server here
    },
    error: function(error) {
      console.log('Send failed: ' + JSON.stringify(error));
      // Handle the error response from the server here
    }
  });

  generateTable(dataRow, currentPage);


}

// Call the function to initialize the table and store the original data
initializeTable();

// Call the function to populate the table initially with the original data
generateTable(originalData);

// Search functionality
const searchInput = document.querySelector('#search-input');
searchInput.addEventListener('keyup', () => {
  const searchText = searchInput.value.trim();
  searchTable(searchText);
});

// Sort functionality
const sortableHeaders = document.querySelectorAll('#datatable th.sortable');
sortableHeaders.forEach(header => {
  header.addEventListener('click', () => {
    const column = header.dataset.column;
    const isNumeric = header.classList.contains('numeric');
    sortTable(column, isNumeric);
  });
});