<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Thực hiện cấp phát tài sản</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <!-- DataTables styles for modal list -->
        <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
        <style>
            .asset-item:hover {
                background-color: #f8f9fa;
                border-color: #0d6efd;
            }
            /* List-style asset items should also highlight */
            #assetList .list-group-item.asset-item:hover {
                background-color: #f8f9fa;
                cursor: pointer;
            }
            .already-allocated-box {
                border: 1px solid #d1ecf1;
                background-color: #f8fcff;
            }
            /* Previously added scroll CSS for DataTables; removed to avoid structural issues */

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
                        <h1 class="h3 mb-4 text-gray-800">
                            <i class="fas fa-clipboard-check text-primary"></i> Cấp phát tài sản cho phiếu: ${req.requestCode}
                        </h1>

                        <form action="${pageContext.request.contextPath}/staff/allocate-assets" method="post">
                            <input type="hidden" name="requestId" value="${req.requestId}">

                            <div class="card shadow mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">Thông tin yêu cầu</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Người yêu cầu:</strong> ${req.teacherName}</p>
                                    <p><strong>Mục đích:</strong> ${req.purpose}</p>
                                </div>
                            </div>

                            <c:forEach var="item" items="${neededItems}" varStatus="st">
                                <div class="category-group" data-group="${st.index}" data-category="${item.categoryId}" data-required="${item.quantity}" data-allocated="${item.allocatedQuantity}" data-remaining="${item.remainingQuantity}">
                                    <div class="card shadow mb-4">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-primary category-title"
                                                data-name="${item.categoryName}">
                                                Loại tài sản: ${item.categoryName} 
                                                (Số lượng cần: ${item.quantity} | Đã cấp: ${item.allocatedQuantity} | Còn thiếu: ${item.remainingQuantity})
                                            </h6>
                                        </div>

                                        <div class="card-body">
                                            <div class="alert alert-warning d-none category-warning" data-group="${st.index}" role="alert">
                                                <span class="category-warning-text"></span>
                                                <button type="button" class="close category-warning-close" data-group="${st.index}" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <c:set var="allocatedList" value="${allocatedAssetsByCategory[item.categoryId]}" />
                                            <div class="already-allocated-box rounded p-3 mb-3">
                                                <div class="font-weight-bold text-info mb-2">
                                                    Tài sản đã phân phối (${item.allocatedQuantity}/${item.quantity})
                                                </div>
                                                <c:choose>
                                                    <c:when test="${not empty allocatedList}">
                                                        <ul class="list-group list-group-flush">
                                                            <c:forEach var="asset" items="${allocatedList}">
                                                                <li class="list-group-item px-0 py-1 border-0 bg-transparent">
                                                                    <strong>${asset.assetCode}</strong> - ${asset.assetName}
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="small text-muted">Chưa có tài sản nào được phân phối cho loại này.</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <c:if test="${item.remainingQuantity > 0}">
                                                <p class="small text-muted">Vui lòng chọn đủ ${item.remainingQuantity} tài sản từ kho bên dưới:</p>
                                            </c:if>

                                            <button type="button"
                                                    class="btn ${item.remainingQuantity > 0 ? 'btn-primary' : 'btn-secondary'} btn-select-assets" 
                                                    data-category="${item.categoryId}" 
                                                    data-limit="${item.remainingQuantity}" 
                                                    data-group="${st.index}"
                                                    ${item.remainingQuantity <= 0 ? 'disabled="disabled"' : ''}>
                                                <i class="fas ${item.remainingQuantity > 0 ? 'fa-plus' : 'fa-check'}"></i>
                                                ${item.remainingQuantity > 0 ? 'Chọn Tài Sản' : 'Đã cấp đủ'}
                                            </button>

                                            <div class="selected-assets mt-3" data-group="${st.index}">
                                                <!-- Selected assets will be displayed here -->
                                            </div>
                                        </div>     

                                    </div>
                                </div>
                            </c:forEach>

                            <div class="mb-5">
                                <label class="form-label">Ghi chú phản hồi</label>
                                <textarea name="note" class="form-control" rows="3" placeholder="Notes..."></textarea>
                            </div>

                            <div class="mb-5">
                                <a href="${pageContext.request.contextPath}/staff/request-list" class="btn btn-secondary">Quay lại</a>
                                <c:if test="${!(req.status == 'OUT_OF_STOCK' || req.status == 'INCOMPLETE')}">
                                    <button type="submit"
                                            class="btn btn-warning shadow-sm"
                                            name="action"
                                            value="notify_out_of_stock"
                                            formnovalidate
                                            onclick="return confirm('Gửi thông báo hết tài sản cho giáo viên ?');">
                                        <i class="fas fa-bell fa-sm text-white-50"></i> Thông báo hết tài sản
                                    </button>
                                </c:if>
                                <button type="submit" 
                                        id="btnSubmit" 
                                        class="btn btn-primary shadow-sm"
                                        name="action"
                                        value="allocate">
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
        <!-- DataTables scripts -->
        <script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

        <!-- Asset Selection Modal -->
        <div class="modal fade" id="assetModal" tabindex="-1" role="dialog" aria-labelledby="assetModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="assetModalLabel">Chọn Tài Sản</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <input type="text" id="assetSearch" class="form-control" placeholder="Tìm kiếm theo mã hoặc tên tài sản...">
                        </div>
                        <div id="assetList">
                            <div class="table-responsive" style="max-height:300px; overflow-y:auto;">
                                <table class="table table-bordered table-hover" id="assetSelectionTable">
                                    <thead class="thead-light">
                                        <tr>
                                            <th style="width:40px"></th>
                                            <th>Mã tài sản</th>
                                            <th>Tên tài sản</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="asset" items="${availableAssets}">
                                            <tr class="asset-item" data-category="${asset.categoryId}" data-code="${asset.assetCode}" data-name="${asset.assetName}">
                                                <td>
                                                    <div class="custom-control custom-checkbox">
                                                        <input type="checkbox" class="custom-control-input modal-asset-check" 
                                                               id="modal_asset_${asset.assetId}" 
                                                               value="${asset.assetId}"
                                                               data-category="${asset.categoryId}">
                                                        <label class="custom-control-label" for="modal_asset_${asset.assetId}"></label>
                                                    </div>
                                                </td>
                                                <td><strong>${asset.assetCode}</strong></td>
                                                <td>${asset.assetName}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="confirmSelection">Xác Nhận Chọn</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
                                                let currentGroup = null;
                                                let currentCategory = null;
                                                let currentLimit = null;

                                                let selectedAssets = {}; // group -> assets

                                                let submitAction = null;

                                                $(document).ready(function () {

                                                    // init groups
                                                    $('.category-group').each(function () {
                                                        let group = $(this).data('group');
                                                        selectedAssets[group] = [];
                                                    });
                                                    updateCounter();

                                                    // open modal
                                                    $('.btn-select-assets').click(function () {

                                                        currentGroup = $(this).data('group');
                                                        currentCategory = $(this).data('category');
                                                        currentLimit = parseInt($(this).data('limit')) || 0;
                                                        hideGroupWarning(currentGroup);

                                                        if (currentLimit <= 0) {
                                                            showGroupWarning(currentGroup, "Loai tai san nay da cap du, khong can chon them.");
                                                            return;
                                                        }

                                                        initSelectionTable();

                                                        // show assets by category
                                                        $('#assetSelectionTable tbody tr').hide();
                                                        $('#assetSelectionTable tbody tr[data-category="' + currentCategory + '"]').show();

                                                        // reset checkbox
                                                        $('.modal-asset-check').prop('checked', false);

                                                        selectedAssets[currentGroup].forEach(a => {
                                                            $('#modal_asset_' + a.assetId).prop('checked', true);
                                                        });

                                                        updateModalDisabledAssets();

                                                        $('#assetSearch').val('');
                                                        $('#assetModal').modal('show');
                                                    });

                                                    // search
                                                    $('#assetSearch').on('input', function () {

                                                        let search = $(this).val().toLowerCase();

                                                        $('#assetSelectionTable tbody tr[data-category="' + currentCategory + '"]').each(function () {

                                                            let code = $(this).data('code').toLowerCase();
                                                            let name = $(this).data('name').toLowerCase();

                                                            $(this).toggle(code.includes(search) || name.includes(search));

                                                        });

                                                    });

                                                    // click row
                                                    $(document).on('click', '#assetSelectionTable .asset-item', function (e) {

                                                        if (!$(e.target).is('input') && !$(e.target).is('label')) {

                                                            let check = $(this).find('.modal-asset-check');

                                                            check.prop('checked', !check.prop('checked')).trigger('change');

                                                        }

                                                    });

                                                    // checkbox change
                                                    $(document).on('change', '.modal-asset-check', function () {

                                                        let assetId = parseInt($(this).val());
                                                        let row = $(this).closest('tr');

                                                        let asset = {
                                                            assetId: assetId,
                                                            assetCode: row.data('code'),
                                                            assetName: row.data('name')
                                                        };

                                                        if ($(this).is(':checked')) {

                                                            if (currentLimit <= 0) {
                                                                showGroupWarning(currentGroup, "Loai tai san nay da cap du, khong can chon them.");
                                                                $(this).prop('checked', false);
                                                                return;
                                                            }

                                                            // check duplicate across groups
                                                            if (isAssetUsed(assetId)) {

                                                                alert("Tài sản này đã được chọn ở nhóm khác.");
                                                                $(this).prop('checked', false);
                                                                return;

                                                            }

                                                            if (selectedAssets[currentGroup].length >= currentLimit) {
                                                                if (currentLimit <= 0) {
                                                                    showGroupWarning(currentGroup, "Loai tai san nay da cap du, khong can chon them.");
                                                                } else {
                                                                    alert("Chỉ được chọn tối đa " + currentLimit + " tài sản.");
                                                                }
                                                                $(this).prop('checked', false);
                                                                return;

                                                            }

                                                            selectedAssets[currentGroup].push(asset);

                                                        } else {

                                                            selectedAssets[currentGroup] =
                                                                    selectedAssets[currentGroup].filter(a => a.assetId !== assetId);

                                                        }

                                                        updateCounter();

                                                    });

                                                    // confirm
                                                    $('#confirmSelection').click(function () {

                                                        updateSelectedAssetsDisplay();
                                                        updateCounter();
                                                        $('#assetModal').modal('hide');

                                                    });

                                                    // track which submit button was clicked
                                                    $('button[type="submit"]').on('click', function () {
                                                        submitAction = $(this).val();
                                                    });

                                                    $(document).on('click', '.category-warning-close', function () {
                                                        let group = $(this).data('group');
                                                        hideGroupWarning(group);
                                                    });

                                                    // submit
                                                    $('form').submit(function (e) {

                                                        if (!submitAction) {
                                                            submitAction = 'allocate';
                                                        }

                                                        // Only require asset selection for allocate action
                                                        if (submitAction !== 'allocate') {
                                                            return;
                                                        }

                                                        let allAssets = [];

                                                        Object.values(selectedAssets).forEach(list => {

                                                            list.forEach(a => allAssets.push(a.assetId));

                                                        });

                                                        if (allAssets.length === 0) {

                                                            e.preventDefault();
                                                            alert("Vui lòng chọn ít nhất một tài sản.");
                                                            return;

                                                        }

                                                        $('input[name="selectedAssetIds"]').remove();

                                                        allAssets.forEach(id => {

                                                            $('<input>').attr({
                                                                type: 'hidden',
                                                                name: 'selectedAssetIds',
                                                                value: id
                                                            }).appendTo('form');

                                                        });

                                                    });

                                                });

                                                function isAssetUsed(assetId) {

                                                    let used = false;

                                                    Object.values(selectedAssets).forEach(list => {

                                                        list.forEach(a => {

                                                            if (a.assetId === assetId)
                                                                used = true;

                                                        });

                                                    });

                                                    return used;

                                                }

                                                function updateSelectedAssetsDisplay() {

                                                    $('.category-group').each(function () {

                                                        let group = $(this).data('group');
                                                        let container = $('.selected-assets[data-group="' + group + '"]');

                                                        let html = '<ul class="list-group">';

                                                        selectedAssets[group].forEach(a => {

                                                            html += '<li class="list-group-item">' +
                                                                    '<strong>' + a.assetCode + '</strong> - ' + a.assetName +
                                                                    '</li>';

                                                        });

                                                        html += '</ul>';

                                                        container.html(html);

                                                    });

                                                }

                                                function updateCounter() {

                                                    $(".category-group").each(function () {

                                                        let group = $(this).data("group");
                                                        let required = $(this).data("required");
                                                        let allocated = $(this).data("allocated");
                                                        let remaining = $(this).data("remaining");
                                                        let count = selectedAssets[group].length;

                                                        let name = $(this).find(".category-title").data("name");

                                                        $(this).find(".category-title")
                                                                .text("Loại tài sản: " + name
                                                                        + " (Số lượng cần: " + required
                                                                        + " | Đã cấp: " + allocated
                                                                        + " | Còn thiếu: " + remaining
                                                                        + " | Đã chọn: " + count + "/" + remaining + ")");

                                                    });

                                                }

                                                function updateModalDisabledAssets() {

                                                    if (currentLimit <= 0) {
                                                        $('.modal-asset-check').prop('disabled', true);
                                                        return;
                                                    }

                                                    $('.modal-asset-check').each(function () {

                                                        let id = parseInt($(this).val());

                                                        if (isAssetUsed(id) &&
                                                                !selectedAssets[currentGroup].some(a => a.assetId === id)) {

                                                            $(this).prop('disabled', true);

                                                        } else {

                                                            $(this).prop('disabled', false);

                                                        }

                                                    });

                                                }

                                                function showGroupWarning(group, message) {
                                                    let warningBox = $('.category-warning[data-group="' + group + '"]');
                                                    warningBox.find('.category-warning-text').text(message);
                                                    warningBox.removeClass('d-none');
                                                }

                                                function hideGroupWarning(group) {
                                                    let warningBox = $('.category-warning[data-group="' + group + '"]');
                                                    warningBox.addClass('d-none');
                                                    warningBox.find('.category-warning-text').text('');
                                                }

                                                function initSelectionTable() {

                                                    if (!$.fn.DataTable.isDataTable('#assetSelectionTable')) {

                                                        $('#assetSelectionTable').DataTable({
                                                            paging: false,
                                                            info: false,
                                                            searching: false,
                                                            order: []
                                                        });

                                                    }

                                                }
        </script>

    </body>
</html>



