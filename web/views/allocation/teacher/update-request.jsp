<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Update Asset Request</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <style>
            .request-container {
                max-width: 800px;
                margin: 30px auto;
            }
            .item-row {
                border-bottom: 1px solid #dee2e6;
                padding: 10px 0;
            }
        </style>
    </head>
    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/allocation/topbar2.jsp" %>

                    <div class="container request-container">
                        <div class="card shadow">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0">Update Asset Request</h4>
                            </div>
                            <div class="card-body">

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                        <i class="fas fa-exclamation-triangle"></i>
                                        ${error}
                                    </div>
                                </c:if>

                                <form action="${pageContext.request.contextPath}/teacher/update-request" method="post">
                                    <input type="hidden" name="requestId" value="${req.requestId}">

                                    <div class="row mb-4">
                                        <div class="col-md-6">
                                            <label class="form-label">Requested room:</label>
                                            <select name="requestedRoomId" class="form-select" required>
                                                <option value="">-- Select room --</option>
                                                <c:forEach var="room" items="${rooms}">
                                                    <option value="${room.roomId}" ${room.roomId == req.requestedRoomId ? 'selected' : ''}>
                                                        ${room.roomName} (${room.roomCode})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Purpose:</label>
                                            <input type="text" name="purpose" class="form-control" value="${req.purpose}" required>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5>Requested items</h5>

                                    <div id="itemList">
                                        <c:choose>
                                            <c:when test="${not empty itemList}">
                                                <c:forEach var="item" items="${itemList}" varStatus="st">
                                                    <div class="row item-row mb-3 align-items-end">
                                                        <div class="col-md-4">
                                                            <label class="form-label">Category:</label>
                                                            <select name="categoryIds" class="form-select" required>
                                                                <option value="">-- Select category --</option>
                                                                <c:forEach var="cat" items="${categories}">
                                                                    <option value="${cat.categoryId}" ${cat.categoryId == item.categoryId ? 'selected' : ''}>
                                                                        ${cat.categoryName}
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">Quantity:</label>
                                                            <input type="number" name="quantities" class="form-control" min="1" value="${item.quantity}" required>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">Note:</label>
                                                            <input type="text" name="notes" class="form-control" value="${item.note}">
                                                        </div>
                                                        <div class="col-md-2">
                                                            <button type="button" class="btn btn-sm btn-outline-danger deleteBtn ${st.first ? 'd-none' : ''}" onclick="deleteItem(this)" title="Delete row">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="row item-row mb-3 align-items-end">
                                                    <div class="col-md-4">
                                                        <label class="form-label">Category:</label>
                                                        <select name="categoryIds" class="form-select" required>
                                                            <option value="">-- Select category --</option>
                                                            <c:forEach var="cat" items="${categories}">
                                                                <option value="${cat.categoryId}">${cat.categoryName}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label class="form-label">Quantity:</label>
                                                        <input type="number" name="quantities" class="form-control" min="1" value="1" required>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label class="form-label">Note:</label>
                                                        <input type="text" name="notes" class="form-control">
                                                    </div>
                                                    <div class="col-md-2">
                                                        <button type="button" class="btn btn-sm btn-outline-danger deleteBtn d-none" onclick="deleteItem(this)" title="Delete row">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="mt-3">
                                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="addItem()">+ Add item</button>
                                    </div>

                                    <div class="mt-5 text-end">
                                        <a href="${pageContext.request.contextPath}/teacher/request-detail?id=${req.requestId}" class="btn btn-light">Cancel</a>
                                        <button type="submit" class="btn btn-success">Update request</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

        <script>
            function addItem() {
                const itemList = document.getElementById('itemList');
                const firstRow = itemList.querySelector('.item-row');
                const newRow = firstRow.cloneNode(true);

                newRow.querySelectorAll('input').forEach(input => input.value = (input.type === 'number' ? 1 : ''));
                newRow.querySelectorAll('select').forEach(select => select.selectedIndex = 0);

                const deleteBtn = newRow.querySelector('.deleteBtn');
                if (deleteBtn) {
                    deleteBtn.classList.remove('d-none');
                }

                itemList.appendChild(newRow);
                updateDeleteButtons();
            }

            function deleteItem(button) {
                const itemRow = button.closest('.item-row');
                const itemList = document.getElementById('itemList');
                const itemCount = itemList.querySelectorAll('.item-row').length;

                if (itemCount <= 1) {
                    alert('At least one item is required.');
                    return;
                }

                itemRow.remove();
                updateDeleteButtons();
            }

            function updateDeleteButtons() {
                const itemList = document.getElementById('itemList');
                const items = itemList.querySelectorAll('.item-row');

                items.forEach((item) => {
                    const deleteBtn = item.querySelector('.deleteBtn');
                    if (items.length === 1) {
                        deleteBtn.classList.add('d-none');
                    } else {
                        deleteBtn.classList.remove('d-none');
                    }
                });
            }
        </script>
    </body>
</html>
