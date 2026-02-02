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

                    <%@ include file="/views/layout/topbar.jsp" %>

                    <!-- Page Content -->
                    <c:if test="${param.msg eq 'success'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            Gửi yêu cầu tài sản thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${param.msg eq 'error'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            Gửi yêu cầu tài sản thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <div class="container request-container">
                        <div class="card shadow">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0">Phiếu Yêu Cầu Tài Sản</h4>
                            </div>
                            <div class="card-body">

                                <!-- Message Alert -->
                                <c:if test="${param.msg eq 'success'}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <i class="fas fa-check-circle"></i>
                                        Gửi yêu cầu tài sản thành công!
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <i class="fas fa-exclamation-triangle"></i>
                                        ${error}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
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

                                    <div id="itemList">
                                        <div class="row item-row mb-3 align-items-end">
                                            <div class="col-md-5">
                                                <label class="form-label">Loại tài sản:</label>
                                                <select name="categoryIds" class="form-select" required>
                                                    <option value="">-- Chọn loại --</option>
                                                    <c:forEach var="cat" items="${categories}">
                                                        <option value="${cat.categoryId}">${cat.categoryName}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label">Số lượng:</label>
                                                <input type="number" name="quantities" class="form-control" min="1" value="1" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label class="form-label">Ghi chú:</label>
                                                <input type="text" name="notes" class="form-control" placeholder="Mô tả thêm">
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



        <script>
            function addItem() {
                const itemList = document.getElementById('itemList');
                const firstRow = itemList.querySelector('.item-row');
                const newRow = firstRow.cloneNode(true);

                // Reset giá trị các input trong dòng mới
                newRow.querySelectorAll('input').forEach(input => input.value = (input.type === 'number' ? 1 : ''));
                newRow.querySelectorAll('select').forEach(select => select.selectedIndex = 0);

                itemList.appendChild(newRow);
            }
        </script>

    </body>
</html>