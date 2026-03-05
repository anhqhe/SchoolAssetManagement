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
                                <div class="category-group" data-group="${st.index}" data-category="${item.categoryId}" data-limit="${item.quantity}">
                                    <div class="card shadow mb-4">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-primary">
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

                                            <button type="button" class="btn btn-primary btn-select-assets" 
                                                    data-category="${item.categoryId}" 
                                                    data-limit="${item.quantity}" 
                                                    data-group="${st.index}">
                                                <i class="fas fa-plus"></i> Chọn Tài Sản
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
                                <textarea name="note" class="form-control" rows="3" placeholder="Notes..." required></textarea>
                            </div>

                            <div class="mb-5">
                                <a href="${pageContext.request.contextPath}/staff/request-list" class="btn btn-secondary">Quay lại</a>
                                <c:if test="${!(req.status == 'OUT_OF_STOCK')}">
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
                                                let selectedAssets = {}; // group -> array of asset objects

                                                $(document).ready(function () {
                                                    // Initialize selectedAssets for each group
                                                    $('.category-group').each(function() {
                                                        let group = $(this).attr('data-group');
                                                        selectedAssets[group] = [];
                                                    });

                                                    $('.btn-select-assets').on('click', function() {
                                                        currentGroup = $(this).attr('data-group');
                                                        currentCategory = $(this).attr('data-category');
                                                        currentLimit = $(this).attr('data-limit');
                                                        
                                                        // initialize DataTable (first time only)
                                                        initSelectionTable();
                                                        
                                                        // Show only assets of this category
                                                        $('.asset-item').hide();
                                                        $('.asset-item[data-category="' + currentCategory + '"]').show();
                                                        
                                                        // Check selected ones
                                                        $('.modal-asset-check').prop('checked', false);
                                                        selectedAssets[currentGroup].forEach(function(asset) {
                                                            $('#modal_asset_' + asset.assetId).prop('checked', true);
                                                        });
                                                        
                                                        $('#assetSearch').val('');
                                                        $('#assetModal').modal('show');
                                                    });

                                                    $('#assetSearch').on('input', function() {
                                                        let search = $(this).val().toLowerCase();
                                                        $('#assetSelectionTable tbody tr[data-category="' + currentCategory + '"]').each(function() {
                                                            let code = $(this).attr('data-code').toLowerCase();
                                                            let name = $(this).attr('data-name').toLowerCase();
                                                            if (code.includes(search) || name.includes(search)) {
                                                                $(this).show();
                                                            } else {
                                                                $(this).hide();
                                                            }
                                                        });
                                                    });

                                                    // allow clicking on the whole row to toggle selection
                                                    $(document).on('click', '#assetSelectionTable .asset-item', function(e) {
                                                        if (!$(e.target).is('input') && !$(e.target).is('label')) {
                                                            let $check = $(this).find('.modal-asset-check');
                                                            $check.prop('checked', !$check.prop('checked')).trigger('change');
                                                        }
                                                    });

                                                    $(document).on('change', '.modal-asset-check', function() {
                                                        let assetId = parseInt($(this).val());
                                                        let $row = $(this).closest('tr');
                                                        let asset = {
                                                            assetId: assetId,
                                                            assetCode: $row.find('td:nth-child(2)').text(),
                                                            assetName: $row.find('td:nth-child(3)').text()
                                                        };
                                                        if ($(this).is(':checked')) {
                                                            if (selectedAssets[currentGroup].length < currentLimit) {
                                                                selectedAssets[currentGroup].push(asset);
                                                            } else {
                                                                $(this).prop('checked', false);
                                                                alert('Đã chọn đủ ' + currentLimit + ' tài sản.');
                                                            }
                                                        } else {
                                                            selectedAssets[currentGroup] = selectedAssets[currentGroup].filter(a => a.assetId !== assetId);
                                                        }
                                                    });

                                                    $('#confirmSelection').on('click', function() {
                                                        updateSelectedAssetsDisplay();
                                                        $('#assetModal').modal('hide');
                                                        validateSelection(currentGroup);
                                                    });

                                                    function updateSelectedAssetsDisplay() {
                                                        let html = '<h6>Tài sản đã chọn:</h6><ul class="list-group">';
                                                        selectedAssets[currentGroup].forEach(function(asset) {
                                                            html += '<li class="list-group-item"><strong>' + asset.assetCode + '</strong> - ' + asset.assetName + '</li>';
                                                        });
                                                        html += '</ul>';
                                                        $('.selected-assets[data-group="' + currentGroup + '"]').html(html);
                                                    }

                                                    // initialize DataTable for modal when opened
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

                                                    function validateSelection(groupId) {
                                                        const limit = parseInt($('.category-group[data-group="' + groupId + '"]').attr('data-limit'), 10);
                                                        const selectedCount = selectedAssets[groupId].length;

                                                        if (selectedCount < limit) {
                                                            showWarning(groupId, 'Vui lòng chọn đủ ' + limit + ' tài sản. Hiện tại đã chọn: ' + selectedCount + '/' + limit);
                                                        } else if (selectedCount === limit) {
                                                            hideWarning(groupId);
                                                        }
                                                    }

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

                                                    $('form').on('submit', function (e) {
                                                        // Collect all selectedAssetIds
                                                        let allSelected = [];
                                                        Object.values(selectedAssets).forEach(function(assets) {
                                                            assets.forEach(function(asset) {
                                                                allSelected.push(asset.assetId);
                                                            });
                                                        });
                                                        // Create hidden inputs
                                                        $('input[name="selectedAssetIds"]').remove();
                                                        allSelected.forEach(function(id) {
                                                            $('<input>').attr({
                                                                type: 'hidden',
                                                                name: 'selectedAssetIds',
                                                                value: id
                                                            }).appendTo('form');
                                                        });

                                                        let hasInvalid = false;
                                                        $('.category-group').each(function() {
                                                            let groupId = $(this).attr('data-group');
                                                            let limit = parseInt($(this).attr('data-limit'), 10);
                                                            let selectedCount = selectedAssets[groupId].length;
                                                            if (selectedCount !== limit) {
                                                                hasInvalid = true;
                                                                showWarning(groupId, 'Chưa chọn đủ tài sản. Cần ' + limit + ' nhưng chỉ chọn ' + selectedCount + '.');
                                                            }
                                                        });

                                                        if (hasInvalid) {
                                                            e.preventDefault();
                                                            alert('Vui lòng kiểm tra lại các tài sản đã chọn.');
                                                        }
                                                    });
                                                });
        </script>





    </body>
</html>


