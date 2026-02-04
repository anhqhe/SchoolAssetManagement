<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Thực hiện cấp phát tài sản</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <style>
            .asset-item:hover {
                background-color: #f8f9fa;
                border-color: #0d6efd;
            }

        </style>
    </head>

    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/topbar.jsp" %>

                    <c:if test="${param.msg eq 'success'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            Thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.msg eq 'error'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            Lỗi
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Page Content -->
                    <div class="container mt-5">
                        <h1 class="h3 mb-4 text-gray-800">Cấp phát tài sản cho phiếu: ${requestDetail.requestCode}</h1>

                        <form action="${pageContext.request.contextPath}/staff/allocate-assets" method="post">
                            <input type="hidden" name="requestId" value="${requestDetail.requestId}">

                            <div class="card shadow mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">Thông tin yêu cầu</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Người yêu cầu:</strong> ${requestDetail.teacherName}</p>
                                    <p><strong>Mục đích:</strong> ${requestDetail.purpose}</p>
                                </div>
                            </div>

                            <c:forEach var="item" items="${neededItems}">
                                <div class="card shadow mb-4 border-left-success">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-success">
                                            Loại tài sản: ${item.categoryName} (Số lượng cần: ${item.quantity})
                                        </h6>
                                    </div>

                                    <div class="card-body">
                                        <p class="small text-muted">Vui lòng chọn đủ ${item.quantity} tài sản từ kho bên dưới:</p>

                                        <div class="row g-3">
                                            <c:forEach var="asset" items="${availableAssets}">
                                                <c:if test="${asset.categoryId == item.categoryId}">
                                                    <div class="col-md-4 mb-2">
                                                        <div class="border rounded p-2 h-100 shadow-sm asset-item">
                                                            <div class="custom-control custom-checkbox">
                                                                <input type="checkbox" class="custom-control-input asset-check" 
                                                                       id="asset_${asset.assetId}" 
                                                                       name="selectedAssetIds" 
                                                                       value="${asset.assetId}"
                                                                       data-category="${item.categoryId}"
                                                                       data-limit="${item.quantity}">
                                                                <label class="custom-control-label" for="asset_${asset.assetId}">
                                                                    <strong>${asset.assetCode}</strong> - ${asset.assetName}
                                                                </label>
                                                            </div>
                                                        </div> 

                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>     

                                </div>
                            </c:forEach>

                            <div class="mb-5">
                                <label class="form-label">Ghi chú phản hồi</label>
                                <textarea name="note" class="form-control" rows="3" placeholder="Notes..." required></textarea>
                            </div>

                            <div class="mb-5">
                                <a href="allocation-list" class="btn btn-secondary">Quay lại</a>
                                <button type="submit" id="btnSubmit" class="btn btn-primary shadow-sm">
                                    <i class="fas fa-check fa-sm text-white-50"></i> Xác nhận bàn giao
                                </button>
                            </div>
                        </form>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <%@ include file="/views/layout/allocation/notification.jsp" %>

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

        <!--<!-- Đang lỗi, không chặn được check quá số lượng -->
        <script>
            $(document).ready(function () {

                $('.asset-check').on('click', function (e) {
                    let categoryId = $(this).data('category');
                    let limit = $(this).data('limit');

                    let $checks = $(`.asset-check[data-category="${categoryId}"]`);
                    let checkedCount = $checks.filter(':checked').length;

                    // Nếu checkbox này CHƯA check mà đã đủ limit → chặn
                    if (!$(this).is(':checked') && checkedCount >= limit) {
                        e.preventDefault(); // CHẶN TRƯỚC KHI CHECK
                        return false;
                    }
                });

            });
        </script>



    </body>
</html>