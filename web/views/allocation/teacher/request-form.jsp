<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>
            <c:choose>
                <c:when test="${not empty req}">Phiếu yêu cầu tài sản</c:when>
                <c:otherwise>Phiếu yêu cầu tài sản</c:otherwise>
            </c:choose>
        </title>
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
                                <h4 class="mb-0">
                                    Phiếu yêu cầu tài sản
                                </h4>
                            </div>
                            <div class="card-body">

                                <!--  Messages -->
                                <c:if test="${not empty sessionScope.message}">
                                    <div class="alert alert-${sessionScope.type eq 'error' ? 'danger' : (sessionScope.type eq 'warning' ? 'warning' : (sessionScope.type eq 'info' ? 'info' : 'success'))} alert-dismissible fade show">
                                        <i class="fas fa-${sessionScope.type eq 'error' ? 'exclamation-circle' : (sessionScope.type eq 'warning' ? 'exclamation-triangle' : (sessionScope.type eq 'info' ? 'info-circle' : 'check-circle'))}"></i>
                                        ${sessionScope.message}
                                        <button type="button" class="close" data-dismiss="alert">
                                            <span>&times;</span>
                                        </button>
                                    </div>
                                    <c:remove var="type" scope="session" />
                                    <c:remove var="message" scope="session" />
                                </c:if>

                                <form action="${pageContext.request.contextPath}${not empty req ? '/teacher/update-request' : '/teacher/add-request'}" method="post">
                                    <c:if test="${not empty req}">
                                        <input type="hidden" name="requestId" value="${req.requestId}">
                                    </c:if>

                                    <div class="row mb-4">
                                        <div class="col-md-6">
                                            <label class="form-label">
                                                Phòng nhận tài sản:
                                            </label>
                                            <select name="requestedRoomId" class="form-select" required>
                                                <option value="">-- Chọn phòng --</option>
                                                <c:forEach var="room" items="${rooms}">
                                                    <option value="${room.roomId}" ${room.roomId == req.requestedRoomId ? 'selected' : ''}>
                                                        ${room.roomName} (${room.roomCode})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Mục đích sử dụng:</label>
                                            <input type="text" name="purpose" class="form-control" value="${not empty req ? req.purpose : ''}" placeholder="Ví dụ: Giảng dạy môn Tin học" required>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5>Danh sách tài sản cần mượn</h5>

                                    <div id="category-dup-warning" class="alert alert-warning d-none" role="alert">
                                        <span id="category-dup-text"></span>
                                        <button type="button" class="close" id="category-dup-close" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>

                                    <div id="itemList">
                                        <c:choose>
                                            <c:when test="${not empty req and not empty itemList}">
                                                <c:forEach var="item" items="${itemList}" varStatus="st">
                                                    <div class="row item-row mb-3 align-items-end">
                                                        <div class="col-md-4">
                                                            <label class="form-label">Loại tài sản:</label>
                                                            <select name="categoryIds" class="form-select" required>
                                                                <option value="">-- Chọn loại --</option>
                                                                <c:forEach var="cat" items="${categories}">
                                                                    <option value="${cat.categoryId}" ${cat.categoryId == item.categoryId ? 'selected' : ''}>
                                                                        ${cat.categoryName}
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">Số lượng:</label>
                                                            <input type="number" name="quantities" class="form-control" min="1" value="${item.quantity}" required>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">Ghi chú:</label>
                                                            <input type="text" name="notes" class="form-control" value="${item.note}" placeholder="Mô tả thêm">
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
                                                        <label class="form-label">Loại tài sản:</label>
                                                        <select name="categoryIds" class="form-select" required>
                                                            <option value="">-- Chọn loại --</option>
                                                            <c:forEach var="cat" items="${categories}">
                                                                <option value="${cat.categoryId}">${cat.categoryName}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label class="form-label">Số lượng:</label>
                                                        <input type="number" name="quantities" class="form-control" min="1" value="1" required>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <label class="form-label">Ghi chú:</label>
                                                        <input type="text" name="notes" class="form-control" placeholder="Mô tả thêm">
                                                    </div>
                                                    <div class="col-md-2">
                                                        <button type="button" class="btn btn-sm btn-outline-danger deleteBtn d-none" onclick="deleteItem(this)" title="XÃ³a dÃ²ng">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="mt-3">
                                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="addItem()">
                                            + Thêm tài sản
                                        </button>
                                    </div>

                                    <div class="mt-5 text-end">
                                        <c:choose>
                                            <c:when test="${not empty req}">
                                                <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-light">Hủy bỏ</a>
                                                <button type="submit" class="btn btn-success">Cập Nhật Yêu Cầu</button>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-light">Hủy bỏ</a>
                                                <button type="submit" class="btn btn-success">Gửi Yêu Cầu</button>
                                            </c:otherwise>
                                        </c:choose>
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
                                            function showDupWarning(message) {
                                                const warning = document.getElementById('category-dup-warning');
                                                const text = document.getElementById('category-dup-text');
                                                text.textContent = message;
                                                warning.classList.remove('d-none');
                                            }

                                            function hideDupWarning() {
                                                const warning = document.getElementById('category-dup-warning');
                                                const text = document.getElementById('category-dup-text');
                                                warning.classList.add('d-none');
                                                text.textContent = '';
                                            }

                                            function hasDuplicateCategories() {
                                                const selects = document.querySelectorAll('select[name="categoryIds"]');
                                                const seen = {};
                                                for (let i = 0; i < selects.length; i++) {
                                                    const value = selects[i].value;
                                                    if (!value)
                                                        continue;
                                                    if (seen[value]) {
                                                        return true;
                                                    }
                                                    seen[value] = true;
                                                }
                                                return false;
                                            }

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
                                                hideDupWarning();
                                            }

                                            function deleteItem(button) {
                                                const itemRow = button.closest('.item-row');
                                                const itemList = document.getElementById('itemList');
                                                const itemCount = itemList.querySelectorAll('.item-row').length;

                                                if (itemCount <= 1) {
                                                    alert('Phải có ít nhất 1 dòng yêu cầu!');
                                                    return;
                                                }

                                                itemRow.remove();
                                                updateDeleteButtons();
                                                hideDupWarning();
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

                                            document.addEventListener('change', function (e) {
                                                if (e.target && e.target.matches('select[name="categoryIds"]')) {
                                                    if (hasDuplicateCategories()) {
                                                        e.target.value = '';
                                                        showDupWarning('Không được chọn trùng loại tài sản trong cùng một yêu cầu.');
                                                    } else {
                                                        hideDupWarning();
                                                    }
                                                }
                                            });

                                            document.addEventListener('submit', function (e) {
                                                if (e.target && e.target.matches('form')) {
                                                    if (hasDuplicateCategories()) {
                                                        e.preventDefault();
                                                        showDupWarning('Không được chọn trùng loại tài sản trong cùng một yêu cầu.');
                                                    }
                                                }
                                            });

                                            document.getElementById('category-dup-close').addEventListener('click', function () {
                                                hideDupWarning();
                                            });
        </script>
    </body>
</html>

