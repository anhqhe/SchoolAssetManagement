<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Yêu cầu tài sản</title>
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

                    <!-- Page Content -->

                    <div class="container request-container">
                        <div class="card shadow">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0">Phiếu Yêu Cầu Tài Sản</h4>
                            </div>
                            <div class="card-body">

                                <!-- Message Alert -->
                                <c:if test="${param.msg eq 'success'}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                        <i class="fas fa-check-circle"></i>
                                        Gửi yêu cầu tài sản thành công!
                                    </div>
                                </c:if>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                        <i class="fas fa-exclamation-triangle"></i>
                                        ${error}
                                    </div>
                                </c:if>




                                <form action="${pageContext.request.contextPath}/teacher/add-request" method="post">

                                    <div class="row mb-4">
                                        <div class="col-md-6">
                                            <label class="form-label">Phòng nhận tài sản:</label>
                                            <select name="requestedRoomId" class="form-select" required>
                                                <option value="">-- Chọn phòng --</option>
                                                <c:forEach var="room" items="${rooms}">
                                                    <option value="${room.roomId}">${room.roomName} (${room.roomCode})</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Mục đích sử dụng:</label>
                                            <input type="text" name="purpose" class="form-control" placeholder="Ví dụ: Giảng dạy môn Tin học" required>
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
                                                <button type="button" class="btn btn-sm btn-outline-danger deleteBtn d-none" onclick="deleteItem(this)" title="Xóa dòng">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mt-3">
                                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="addItem()">+ Thêm tài sản</button>
                                    </div>

                                    <div class="mt-5 text-end">
                                        <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-light">Hủy bỏ</a>
                                        <button type="submit" class="btn btn-success">Gửi yêu cầu (Submit)</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>


        <!-- Scripts -->
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
                                                    if (!value) continue;
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

                                                // Reset giá trị các input trong dòng mới
                                                newRow.querySelectorAll('input').forEach(input => input.value = (input.type === 'number' ? 1 : ''));
                                                newRow.querySelectorAll('select').forEach(select => select.selectedIndex = 0);

                                                // Hiện nút xóa cho dòng mới
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

                                                // Ngăn xóa nếu chỉ còn 1 dòng yêu cầu
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

                                                items.forEach((item, index) => {
                                                    const deleteBtn = item.querySelector('.deleteBtn');
                                                    // Ẩn nút xóa nếu chỉ còn 1 dòng
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
