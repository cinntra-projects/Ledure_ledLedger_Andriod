package com.cinntra.ledure.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cinntra.ledure.R;
import com.cinntra.ledure.adapters.ContactPersonAdapter;
import com.cinntra.ledure.adapters.PaymentAdapter;
import com.cinntra.ledure.adapters.SalesEmployeeAdapter;
import com.cinntra.ledure.adapters.WareHouseDropdownAdapter;
import com.cinntra.ledure.fragments.AddQuotationForm_One_Fragment;
import com.cinntra.ledure.globals.Globals;
import com.cinntra.ledure.globals.MainBaseActivity;
import com.cinntra.ledure.interfaces.SubmitQuotation;
import com.cinntra.ledure.model.AddQuotation;
import com.cinntra.ledure.model.BPAddress;
import com.cinntra.ledure.model.BusinessPartnerData;
import com.cinntra.ledure.model.ContactPerson;
import com.cinntra.ledure.model.ContactPersonData;
import com.cinntra.ledure.model.CustomerBusinessRes;
import com.cinntra.ledure.model.DocumentLines;
import com.cinntra.ledure.model.PayMentTerm;
import com.cinntra.ledure.model.QuotationResponse;
import com.cinntra.ledure.model.SalesEmployeeItem;
import com.cinntra.ledure.model.WareHouseData;
import com.cinntra.ledure.model.WareHouseResponse;
import com.cinntra.ledure.newapimodel.NewOpportunityRespose;
import com.cinntra.ledure.viewModel.ItemViewModel;
import com.cinntra.ledure.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddQuotationAct extends MainBaseActivity implements View.OnClickListener, SubmitQuotation {
    public static int PARTNERCODE = 10000;
    public static int OPPCODE = 1001;
    public static int ITEMSCODE = 1000;
    String unitId = "";

    @BindView(R.id.head_title)
    TextView head_title;
    @BindView(R.id.back_press)
    RelativeLayout back_press;

    @BindView(R.id.postingDate)
    LinearLayout postingDate;
    @BindView(R.id.posting_value)
    EditText posting_value;
    @BindView(R.id.valid_till_value)
    EditText valid_till_value;
    @BindView(R.id.validDate)
    LinearLayout validDate;
    @BindView(R.id.document_date_value)
    EditText document_date_value;
    @BindView(R.id.documentDate)
    LinearLayout documentDate;
    @BindView(R.id.remark_value)
    EditText remark_value;
    @BindView(R.id.opportunity_name_value)
    EditText opportunity_name_value;


    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.postCal)
    ImageView postCal;
    @BindView(R.id.validCal)
    ImageView validCal;
    @BindView(R.id.docCal)
    ImageView docCal;
    @BindView(R.id.contact_person_spinner)
    Spinner contact_person_spinner;
    @BindView(R.id.bpView)
    LinearLayout bpView;
    @BindView(R.id.business_partner_value)
    EditText business_partner_value;
    @BindView(R.id.bussinessPartner)
    RelativeLayout bussinessPartner;
    @BindView(R.id.opp_view)
    RelativeLayout opp_view;
    @BindView(R.id.salesemployee_spinner)
    Spinner sales_employee_spinner;
    @BindView(R.id.quo_namevalue)
    EditText quo_namevalue;

    @BindView(R.id.quote_information)
    TextView quote_information;
    @BindView(R.id.quotationName)
    TextView quotationName;

    @BindView(R.id.unit_branch)
    TextView unit_branch;
    @BindView(R.id.payment_term_spinner)
    Spinner payment_term_spinner;
    @BindView(R.id.delivery_mode_spinner)
    Spinner delivery_mode_spinner;
    @BindView(R.id.payment_type_spinner)
    Spinner payment_type_spinner;
    @BindView(R.id.freeDeliveryStatus)
    CheckBox freeDeliveryStatus;


    @BindView(R.id.tvWareHouseList)
    TextView tvWareHouseList;
    @BindView(R.id.spinnerWarehouse)
    Spinner spinnerWarehouse;
    public static String wareHouseId = "";

    public static String CardValue;
    public static String CardName;
    public static String salePCode;
    public static AddQuotation addQuotationObj;
    AppCompatActivity act;
    String OPPID = "";
    public static String salesEmployeeCode = "";
    List<SalesEmployeeItem> salesEmployeeItemList = new ArrayList<>();
    NewOpportunityRespose oppdata;

    public static ArrayList<BPAddress> addressData = new ArrayList<BPAddress>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = AddQuotationAct.this;
        setContentView(R.layout.add_quotation);
        addQuotationObj = new AddQuotation();
        ButterKnife.bind(this);

        callSalessApi();
        setDefaults();
        if (!Prefs.getString(Globals.QuotationListing, "").equalsIgnoreCase("null")) {
            oppdata = Globals.opportunityData.get(0);
            setOppData(oppdata);
        }
     //   warehouseList();


    }

    private ArrayList<WareHouseData> wareHouseList = new ArrayList<>();

    private void warehouseList(String zone) {
        HashMap<String, String> hde = new HashMap<>();
        hde.put("BusinessPlaceID", zone);
        Call<WareHouseResponse> call = NewApiClient.getInstance().getApiService(this).warehouseList(hde);
        call.enqueue(new Callback<WareHouseResponse>() {
            @Override
            public void onResponse(Call<WareHouseResponse> call, Response<WareHouseResponse> response) {
                if (response.code() == 200) {
                    // binding.loader.setVisibility(View.GONE);
                    if (response.body().getData() == null || response.body().getData().size() == 0) {
                        Globals.setmessage(getApplicationContext());
                        //no_datafound.setVisibility(View.VISIBLE);
                    } else {
                        wareHouseList.clear();
                        wareHouseList.addAll(response.body().getData());
                        spinnerWarehouse.setAdapter(new WareHouseDropdownAdapter(AddQuotationAct.this, wareHouseList));
                        spinnerWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                //  Toast.makeText(MainActivity_B2C.this, "click", Toast.LENGTH_SHORT).show();
                                // Toast.makeText(MainActivity_B2C.this, AllitemsList.get(i).getCardName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(MainActivity_B2C.this, adapterView.getItemIdAtPosition(i), Toast.LENGTH_SHORT).show();

                                wareHouseId = wareHouseList.get(i).getWarehouseCode();
                                addQuotationObj.setWarehouseCode(wareHouseId);
                                //  bpFullName = AllitemsList.get(i).getCardName();

                                //  callcontactpersonApi(cp_spinner,filterwithoutprospect(AllitemsList).get(i).getCardCode());
//                                callcontactpersonApi(cp_spinner, AllitemsList.get(i).getCardCode());
//                                bpReSourceID = AllitemsList.get(i).getCardCode();


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                wareHouseId = wareHouseList.get(0).getWarehouseCode();
                                addQuotationObj.setWarehouseCode(wareHouseId);
                            }
                        });

//                        AllItemList.clear();
//                        AllItemList.addAll(response.body().getPendingOrderData().getOrderwise());
//
//                        adapter = new PendingSubListOrderAdapter(PendingOrderSubListActivity.this, AllItemList);
//                        layoutManager = new LinearLayoutManager(PendingOrderSubListActivity.this, RecyclerView.VERTICAL, false);
//                        binding.recyclerview.setAdapter(adapter);
//                        binding.recyclerview.setLayoutManager(layoutManager);


                    }


                }
            }

            @Override
            public void onFailure(Call<WareHouseResponse> call, Throwable t) {
                //  binding.loader.setVisibility(View.GONE);
                Log.e("TAG", "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(AddQuotationAct.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void setOppData(NewOpportunityRespose oppdata) {
        business_partner_value.setClickable(false);
        business_partner_value.setEnabled(false);
        business_partner_value.setTextColor(Color.parseColor(getString(R.color.black)));
        bussinessPartner.setClickable(false);
        bussinessPartner.setEnabled(false);
        opportunity_name_value.setClickable(false);
        opportunity_name_value.setEnabled(false);
        opportunity_name_value.setTextColor(Color.parseColor(getString(R.color.black)));
        opp_view.setClickable(false);
        opp_view.setEnabled(false);
        OPPID = oppdata.getId();
        opportunity_name_value.setText(oppdata.getOpportunityName());
        business_partner_value.setText(oppdata.getCustomerName());

        CardValue = oppdata.getCardCode();
        CardName = oppdata.getCustomerName();
        salePCode = oppdata.getContactPerson();
        salesEmployeeCode = oppdata.getSalesPerson();


        addQuotationObj.setCardCode(CardValue);
        addQuotationObj.setCardName(CardName);
        addQuotationObj.setSalesPerson(salePCode);
        addQuotationObj.setSalesPersonCode(salesEmployeeCode);

        callContactEmployeeApi(oppdata.getCardCode());
    }

    private void callSalessApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getSalesEmployeeList().observe(this, new Observer<List<SalesEmployeeItem>>() {
            @Override
            public void onChanged(@Nullable List<SalesEmployeeItem> itemsList) {
                if (itemsList == null || itemsList.size() == 0) {
                    Globals.setmessage(getApplicationContext());
                } else {
                    salesEmployeeItemList = itemsList;
                    sales_employee_spinner.setAdapter(new SalesEmployeeAdapter(getApplicationContext(), itemsList));
                    // Globals.getSelectedSalesP(salesEmployeeItemList,salesEmployeeCode);
                    if (!Prefs.getString(Globals.QuotationListing, "").equalsIgnoreCase("null"))
                        sales_employee_spinner.setSelection(Globals.getSelectedSalesP(salesEmployeeItemList, salesEmployeeCode));
                    salesEmployeeCode = salesEmployeeItemList.get(0).getSalesEmployeeCode();


                }
            }
        });
    }

    private void setDefaults() {
        bpView.setVisibility(View.VISIBLE);
        head_title.setText(getResources().getString(R.string.add_quotation));
        back_press.setOnClickListener(this);
        postingDate.setOnClickListener(this);
        posting_value.setOnClickListener(this);
        postCal.setOnClickListener(this);
        validDate.setOnClickListener(this);
        valid_till_value.setOnClickListener(this);
        validCal.setOnClickListener(this);
        documentDate.setOnClickListener(this);
        document_date_value.setOnClickListener(this);
        docCal.setOnClickListener(this);
        bussinessPartner.setOnClickListener(this);
        business_partner_value.setOnClickListener(this);
        opportunity_name_value.setOnClickListener(this);
        opp_view.setOnClickListener(this);


        submit.setOnClickListener(this);
        quotationName.setText(R.string.quotation_name_mand);
    }

    private void selectBPartner() {
        Prefs.putString(Globals.BussinessPageType, "Quotation");
        Intent i = new Intent(act, BussinessPartners.class);
        startActivityForResult(i, PARTNERCODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_press:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    Globals.opp = null;
                    onBackPressed();
                }
                break;
            case R.id.postingDate:
            case R.id.postCal:
            case R.id.posting_value:
                Globals.selectDate(act, posting_value);
                break;
            case R.id.validDate:
            case R.id.validCal:
            case R.id.valid_till_value:
                Globals.selectDate(act, valid_till_value);
                break;
            case R.id.documentDate:
            case R.id.document_date_value:
            case R.id.docCal:
                Globals.selectDate(act, document_date_value);
                break;
            case R.id.bussinessPartner:
            case R.id.business_partner_value:
                selectBPartner();
                break;
            case R.id.opportunity_name_value:
            case R.id.opp_view:
                selectOpportunity();
                break;

            case R.id.itemsView:
                if (Globals.SelectedItems.size() == 0) {
                    Intent intent = new Intent(AddQuotationAct.this, ItemsList.class);
                    startActivityForResult(intent, ITEMSCODE);
                } else {
                    Intent intent = new Intent(AddQuotationAct.this, SelectedItems.class);
                    intent.putExtra("FromWhere", "AddQt");
                    startActivityForResult(intent, ITEMSCODE);
                }
                break;
            case R.id.submit:
                String oppname = opportunity_name_value.getText().toString().trim();
                String poDate = posting_value.getText().toString().trim();
                String vDate = valid_till_value.getText().toString().trim();
                String docDate = document_date_value.getText().toString().trim();
                String remark = remark_value.getText().toString().trim();
                if (valiadtion(oppname, contactPersonCode, poDate, vDate, docDate, remark)) {
                    if (!Prefs.getString(Globals.SelectedBranch, "").isEmpty())
                        addQuotationObj.setBPLName(Prefs.getString(Globals.SelectedBranch, ""));
                    if (!Prefs.getString(Globals.SelectedBranchID, "").isEmpty())
                        addQuotationObj.setBPL_IDAssignedToInvoice(Prefs.getString(Globals.SelectedBranchID, ""));


                    addQuotationObj.setOpportunityName(oppname);
                    addQuotationObj.setSalesPerson(contactPersonCode);
                    addQuotationObj.setSalesPersonCode(salesEmployeeCode);
                    addQuotationObj.setPostingDate(poDate);
                    addQuotationObj.setValidDate(vDate);
                    addQuotationObj.setDocumentDate(docDate);
                    addQuotationObj.setRemarks(remark);
                    addQuotationObj.setDeliveryMode(deliveryMode);
                    addQuotationObj.setPayTermsGrpCode(PaymentTermCode);
                    addQuotationObj.setPaymentType(paymentType);
                    addQuotationObj.setFreeDelivery("");
                    addQuotationObj.setTermCondition("");
                    addQuotationObj.setDeliveryCharge("");


                    addQuotationObj.setUnit(unitId);
                    addQuotationObj.setFreeDelivery(freeStatus);
                    addQuotationObj.setPayTermsGrpCode(PaymentTermCode);

                    //  addQuotationObj.setUnit(unit_branch.getText().toString().trim());
                    addQuotationObj.setU_LAT("");
                    addQuotationObj.setLink("");
                    addQuotationObj.setU_LONG("");
                    // addQuotationObj.setDeliveryTerm("");
                    addQuotationObj.setCreatedBy(Prefs.getString(Globals.SalesEmployeeCode, ""));
                    //addQuotationObj.setAdditionalCharges("");
                    addQuotationObj.setU_OPPID(OPPID);
                    addQuotationObj.setUpdateDate(Globals.getTodaysDate());
                    addQuotationObj.setUpdateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateTime(Globals.getTCurrentTime());
                    addQuotationObj.setCreateDate(Globals.getTodaysDate());
                    addQuotationObj.setU_QUOTNM(quo_namevalue.getText().toString().trim());

                    AddQuotationForm_One_Fragment addQuotationForm_one_fragment = new AddQuotationForm_One_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.main_edit_qt_frame, addQuotationForm_one_fragment).addToBackStack("");
                    fragmentTransaction.commit();
                }
//
                break;
        }
    }

    private void selectOpportunity() {
        Prefs.putString(Globals.SelectOpportnity, "Quotation");
        Intent in = new Intent(act, Opportunities_Pipeline_Activity.class);
        startActivityForResult(in, OPPCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PARTNERCODE) {
            BusinessPartnerData customerItem = (BusinessPartnerData) data.getSerializableExtra(Globals.CustomerItemData);
            callparticularcustomerdetails(customerItem.getCardCode());
            //setData(customerItem);
        } else if (resultCode == RESULT_OK && requestCode == OPPCODE) {
            NewOpportunityRespose oppItem = Globals.opp;
            if (oppItem != null) {
//            opportunity_name_value.setText(oppItem.getOpportunityName());
                setOppData(oppItem);
//            OPPID = oppItem.getId();
            }

        }


    }


    String contactPersonCode = "";
    String PaymentTermCode = "";
    String deliveryMode = "";
    String paymentType = "";
    String freeStatus = "0";
    private ArrayList<ContactPersonData> ContactEmployeesList = new ArrayList<>();
    private ArrayList<PayMentTerm> paymentTermList = new ArrayList<>();
    private ContactPersonAdapter contactPersonAdapter;


    private void setData(BusinessPartnerData customerItem) {
        paymentTermList.addAll(customerItem.getPayTermsGrpCode());
        unit_branch.setText(customerItem.getUnitName());
        unitId = customerItem.getUnit();
        addressData.addAll(customerItem.getBPAddresses());
        payment_term_spinner.setAdapter(new PaymentAdapter(AddQuotationAct.this, customerItem.getPayTermsGrpCode()));
        warehouseList(customerItem.getUnit());


        callContactEmployeeApi(customerItem.getCardCode());
        //PriceListNum = customerItem.getPriceListNum();
        CardValue = customerItem.getCardCode();
        CardName = customerItem.getCardName();
        salePCode = customerItem.getContactPerson();
        salesEmployeeCode = customerItem.getSalesPersonCode().get(0).getSalesEmployeeCode();
        addQuotationObj.setCardCode(CardValue);
        addQuotationObj.setCardName(CardName);
        addQuotationObj.setSalesPerson(salePCode);
        addQuotationObj.setSalesPersonCode(salesEmployeeCode);


        //ContactEmployeesList = customerItem.getContactPerson();
        business_partner_value.setText(customerItem.getCardName());
        contact_person_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contactPersonCode = ContactEmployeesList.get(position).getInternalCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                contactPersonCode = ContactEmployeesList.get(0).getInternalCode();
            }
        });
        sales_employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (salesEmployeeItemList.size() > 0 && position > 0)
                    salesEmployeeCode = salesEmployeeItemList.get(position).getSalesEmployeeCode();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                salesEmployeeCode = salesEmployeeItemList.get(0).getSalesEmployeeCode();

            }
        });
        payment_term_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (paymentTermList.size() > 0)
                    PaymentTermCode = paymentTermList.get(i).getGroupNumber();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (paymentTermList.size() > 0)
                    PaymentTermCode = paymentTermList.get(0).getGroupNumber();
            }
        });
        payment_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                paymentType = payment_type_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                paymentType = payment_type_spinner.getSelectedItem().toString();
            }
        });
        delivery_mode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                deliveryMode = delivery_mode_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deliveryMode = delivery_mode_spinner.getSelectedItem().toString();
            }
        });
        freeDeliveryStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    freeStatus = "1";
                else
                    freeStatus = "0";
            }
        });
    }


    private void callContactEmployeeApi(String id) {
        ContactPersonData contactPersonData = new ContactPersonData();
        contactPersonData.setCardCode(id);
        loader.setVisibility(View.VISIBLE);
        Call<ContactPerson> call = NewApiClient.getInstance().getApiService(this).contactemplist(contactPersonData);
        call.enqueue(new Callback<ContactPerson>() {
            @Override
            public void onResponse(Call<ContactPerson> call, Response<ContactPerson> response) {
                loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        ContactEmployeesList.clear();
                        ContactEmployeesList.addAll(response.body().getData());

                    } else {
                        ContactPersonData contactEmployees = new ContactPersonData();
                        contactEmployees.setFirstName("No Contact Person");
                        contactEmployees.setInternalCode("-1");

                        ContactEmployeesList.add(contactEmployees);
                    }
                    contactPersonAdapter = new ContactPersonAdapter(act, ContactEmployeesList);
                    contact_person_spinner.setAdapter(contactPersonAdapter);
                    if (!Prefs.getString(Globals.QuotationListing, "").equalsIgnoreCase("null"))
                        contact_person_spinner.setSelection(Globals.getContactPos(ContactEmployeesList, salePCode));
                    contactPersonCode = ContactEmployeesList.get(0).getInternalCode();

                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddQuotationAct.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactPerson> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toast.makeText(AddQuotationAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    ArrayList<DocumentLines> postlist;

    private ArrayList<DocumentLines> postJson(ArrayList<DocumentLines> list) {
        postlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DocumentLines dc = new DocumentLines();
            if (!Prefs.getString(Globals.SelectedWareHose, "").isEmpty())
                dc.setWarehouseCode(Prefs.getString(Globals.SelectedWareHose, ""));
            dc.setItemCode(Globals.SelectedItems.get(i).getItemCode());
            dc.setQuantity(Globals.SelectedItems.get(i).getQuantity());
            dc.setTaxCode(Globals.SelectedItems.get(i).getTaxCode());//BED+VAT
            dc.setUnitPrice(Globals.SelectedItems.get(i).getUnitPrice());
            dc.setItemDescription(Globals.SelectedItems.get(i).getItemName());
            dc.setDiscountPercent(2.0f);
            postlist.add(dc);
        }

        return postlist;
    }

    private void addQuotation(AddQuotation in, ProgressBar loader) {
        loader.setVisibility(View.VISIBLE);
        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService(this).addQuotation(in);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        Globals.SelectedItems.clear();
                        Globals.opp = null;

                        Toasty.success(AddQuotationAct.this, "Add Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toasty.warning(AddQuotationAct.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(AddQuotationAct.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toasty.error(AddQuotationAct.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void submitQuotaion(ProgressBar loader) {

        addQuotationObj.setDocumentLines(Globals.SelectedItems);
        addQuotation(addQuotationObj, loader);

    }

    private boolean valiadtion(String OppName, String contactPerson, String postDate, String validDate,
                               String DocDate, String remarks) {
     /* if(OppName.isEmpty())
      {
          Globals.showMessage(act,"Enter Opportunity name");
          return false;
      }
      else */
        if (contactPerson.isEmpty()) {
            Globals.showMessage(act, "No Contact Person Found");
            return false;
        } else if (validDate.isEmpty()) {
            Globals.showMessage(act, "Enter Valid date");
            return false;
        } else if (DocDate.isEmpty()) {
            Globals.showMessage(act, "Enter Document date");
            return false;
        } else if (postDate.isEmpty()) {
            Globals.showMessage(act, "Enter Posting date");
            return false;
        } else if (remarks.isEmpty()) {
            Globals.showMessage(act, "Enter Remarks");
            return false;
        }
        return true;
    }


    private void callparticularcustomerdetails(String cardCode) {
        BusinessPartnerData contactPersonData = new BusinessPartnerData();
        contactPersonData.setCardCode(cardCode);
        Call<CustomerBusinessRes> call = NewApiClient.getInstance().getApiService(this).particularcustomerdetails(contactPersonData);
        call.enqueue(new Callback<CustomerBusinessRes>() {
            @Override
            public void onResponse(Call<CustomerBusinessRes> call, Response<CustomerBusinessRes> response) {
                loader.setVisibility(View.GONE);

                if (response.code() == 200) {
                    // loadQuotation(loader, fromWhere, currentPage);
                    setData(response.body().getData().get(0));
                }


            }

            @Override
            public void onFailure(Call<CustomerBusinessRes> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (setAlertDataDiscard(AddQuotationAct.this)) {
            super.onBackPressed();
        }


    }

    boolean Dcstatus = true;

    private boolean setAlertDataDiscard(Context context) {
        Dcstatus = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.data_discard)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Dcstatus = true;
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Dcstatus = false;
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        // alert.setTitle(R.string.data_discard_sub);
        alert.show();
        return Dcstatus;

    }
}