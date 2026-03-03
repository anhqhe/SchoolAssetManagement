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

                    <%@ include file="/views/layout/allocation/topbar2.jsp" %>

                    <!-- Message -->
                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-${sessionScope.type eq 'error' ? 'danger' : (sessionScope.type eq 'warning' ? 'warning' : (sessionScope.type eq 'info' ? 'info' : 'success'))} alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-${sessionScope.type eq 'error' ? 'exclamation-triangle' : (sessionScope.type eq 'warning' ? 'exclamation-triangle' : (sessionScope.type eq 'info' ? 'info-circle' : 'check-circle'))}"></i>
                            ${sessionScope.message}
                        </div>
                        <c:remove var="type" scope="session" />
                        <c:remove var="message" scope="session" />
                    </c:if>

                    <!-- Page Content -->
                    <div class="container mt-5">
                        <h1 class="h3 mb-4 text-gray-800">Cấp phát tài sản cho phiếu: ${requestDetail.requestCode}</h1>

                        <form action="${pageContext.request.contextPath}/staff/allocate-assets" method="post">
                            <input type="hidden" name="requestId" value="${requestDetail.requestId}">
                            <input type="hidden" name="action" id="actionField" value="">

                            <div class="card shadow mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">Thông tin yêu cầu</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Người yêu cầu:</strong> ${requestDetail.teacherName}</p>
                                    <p><strong>Mục đích:</strong> ${requestDetail.purpose}</p>
                                </div>
                            </div>

                            <c:forEach var="item" items="${neededItems}" varStatus="st">
                                <div class="category-group" data-category="${item.categoryId}" data-limit="${item.quantity}">
                                    <div class="card shadow mb-4 border-left-success">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-success">
                                                Loại tài sản: ${item.categoryName} (Số lượng cần: ${item.quantity})
                                            </h6>
                                        </div>

                                        <div class="card-body">
                                            <div class="alert alert-warning d-none category-warning" data-group="${st.index}" role="alert">
                                                <span class="category-warning-text"></span>
                                                <button type="button" class="close category-warning-close" data-group="${st.index}" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
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
                                                                           data-limit="${item.quantity}"
                                                                           data-group="${st.index}">
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
                                </div>
                            </c:forEach>

                            <div class="mb-5">
                                <label class="form-label">Ghi chú phản hồi</label>
                                <textarea name="note" class="form-control" rows="3" placeholder="Notes..." required></textarea>
                            </div>

                            <div class="mb-5">
                                <a href="${pageContext.request.contextPath}/staff/request-list" class="btn btn-secondary">Quay lại</a>
                                <button type="submit"
                                        class="btn btn-warning shadow-sm"
                                        formnovalidate
                                        onclick="document.getElementById('actionField').value = 'notify_out_of_stock'; return confirm('Gửi thông báo hết tài sản cho giáo viên ?');">
                                    <i class="fas fa-bell fa-sm text-white-50"></i> Thông báo hết tài sản
                                </button>
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

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

        <script>
                                            $(document).ready(function () {
                                                function showWarning(groupId, message) {
                                                    const $warning = $('.category-warning[data-group="' + groupId + '"]');
                                                    $warning.find('.category-warning-text').text(message);
                                                    $warning.removeClass('d-none');
                                                }

                                                function hideWarning(groupId) {
                                                    const $warning = $('.category-warning[data-group="' + groupId + '"]');
                                                    $warning.addClass('d-none');
                                                    $warning.find('.category-warning-text').text('');
                                                }

                                                $(document).on('click', '.category-warning-close', function () {
                                                    const groupId = $(this).attr('data-group');
                                                    hideWarning(groupId);
                                                });

                                                $('.asset-check').on('change', function () {
                                                    const groupId = $(this).attr('data-group');
                                                    const $checks = $('.asset-check[data-group="' + groupId + '"]');
                                                    const limit = parseInt($(this).attr('data-limit'), 10);
                                                    const checkedCount = $checks.filter(':checked').length;

                                                    if (Number.isFinite(limit) && $(this).is(':checked') && checkedCount > limit) {
                                                        this.checked = false;
                                                        showWarning(groupId, 'Số lượng tài sản đã chọn vượt quá số lượng yêu cầu.');
                                                        return;
                                                    }
                                                    hideWarning(groupId);
                                                });

                                                $('form').on('submit', function (e) {
                                                    let hasInvalid = false;

                                                    const groupIds = {};
                                                    $('.asset-check').each(function () {
                                                        groupIds[$(this).attr('data-group')] = true;
                                                    });

                                                    Object.keys(groupIds).some(function (groupId) {
                                                        const $checks = $('.asset-check[data-group="' + groupId + '"]');
                                                        const limit = parseInt($checks.first().attr('data-limit'), 10);
                                                        const checkedCount = $checks.filter(':checked').length;
                                                        if (Number.isFinite(limit) && checkedCount > limit) {
                                                            hasInvalid = true;
                                                            showWarning(groupId, 'Số lượng tài sản đã chọn vượt quá số lượng yêu cầu.');
                                                            return true;
                                                        }
                                                        hideWarning(groupId);
                                                        return false;
                                                    });

                                                    if (hasInvalid) {
                                                        e.preventDefault();
                                                    }
                                                });
                                            });
        </script>





    </body>
</html>


