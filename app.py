import mysql.connector
import streamlit as st
import os
import time
from datetime import date

# ── DB Connection ─────────────────────────────────────────────────────────────
def get_connection():
    for attempt in range(10):
        try:
            return mysql.connector.connect(
                host=os.environ.get("DB_HOST", "mysql"),
                user=os.environ.get("DB_USER", "root"),
                password=os.environ.get("DB_PASSWORD", "abdul1234"),
                database=os.environ.get("DB_NAME", "fir_db")
            )
        except mysql.connector.Error:
            time.sleep(3)
    return None

def init_db():
    conn = get_connection()
    if conn:
        cur = conn.cursor()
        cur.execute("""
            CREATE TABLE IF NOT EXISTS fir_records (
                id              INT AUTO_INCREMENT PRIMARY KEY,
                fir_number      VARCHAR(30)  NOT NULL UNIQUE,
                complainant     VARCHAR(100) NOT NULL,
                cnic            VARCHAR(20)  NOT NULL,
                contact         VARCHAR(20)  NOT NULL,
                crime_type      VARCHAR(50)  NOT NULL,
                location        VARCHAR(150) NOT NULL,
                incident_date   DATE         NOT NULL,
                description     TEXT,
                status          VARCHAR(30)  DEFAULT 'Under Investigation',
                officer_name    VARCHAR(100) NOT NULL,
                registered_at   DATETIME     DEFAULT CURRENT_TIMESTAMP
            )
        """)
        conn.commit()
        cur.close()
        conn.close()

# ── Config ────────────────────────────────────────────────────────────────────
st.set_page_config(
    page_title="FIR Management System",
    page_icon="🚔",
    layout="wide"
)
init_db()

# ── Sidebar ───────────────────────────────────────────────────────────────────
st.sidebar.title(" FIR Management")
st.sidebar.markdown("**Punjab Police — Digital FIR Portal**")
st.sidebar.markdown("---")
operation = st.sidebar.selectbox(
    "Select Operation",
    (" View All FIRs", " Register New FIR",
     " Update FIR Status", " Delete FIR Record")
)

CRIME_TYPES = [
    "Theft", "Robbery", "Assault", "Fraud",
    "Kidnapping", "Cybercrime", "Murder",
    "Drug Trafficking", "Vandalism", "Other"
]
STATUSES = [
    "Under Investigation", "Challan Submitted",
    "Court Proceedings", "Case Closed", "Acquitted"
]

st.title(" FIR Record Management System")
st.markdown("**Punjab Police — Digital Crime Registration Portal**")
st.markdown("---")

# ── CREATE ────────────────────────────────────────────────────────────────────
if operation == " Register New FIR":
    st.subheader(" Register a New FIR")

    col1, col2 = st.columns(2)
    with col1:
        fir_number   = st.text_input("FIR Number (e.g. FIR-2026-001)", key="fir_no")
        complainant  = st.text_input("Complainant Full Name", key="complainant")
        cnic         = st.text_input("CNIC (e.g. 37405-1234567-1)", key="cnic")
        contact      = st.text_input("Contact Number", key="contact")
        officer_name = st.text_input("Investigating Officer Name", key="officer")
    with col2:
        crime_type     = st.selectbox("Crime Type", CRIME_TYPES, key="crime_type")
        location       = st.text_input("Incident Location", key="location")
        incident_date  = st.date_input("Incident Date", value=date.today(), key="inc_date")
        description    = st.text_area("Incident Description", key="desc", height=120)

    if st.button(" Register FIR", key="register_btn"):
        if not all([fir_number, complainant, cnic, contact, location, officer_name]):
            st.warning("⚠️ Please fill in all required fields.")
        else:
            conn = get_connection()
            if conn:
                try:
                    cur = conn.cursor()
                    cur.execute("""
                        INSERT INTO fir_records
                        (fir_number, complainant, cnic, contact, crime_type,
                         location, incident_date, description, officer_name)
                        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
                    """, (fir_number, complainant, cnic, contact, crime_type,
                          location, incident_date, description, officer_name))
                    conn.commit()
                    st.success(f" FIR **{fir_number}** registered successfully!")
                except mysql.connector.IntegrityError:
                    st.error(" FIR Number already exists. Use a unique FIR number.")
                finally:
                    cur.close(); conn.close()

# ── READ ──────────────────────────────────────────────────────────────────────
elif operation == " View All FIRs":
    st.subheader(" Registered FIR Records")

    col1, col2 = st.columns(2)
    with col1:
        filter_crime = st.selectbox("Filter by Crime Type", ["All"] + CRIME_TYPES)
    with col2:
        filter_status = st.selectbox("Filter by Status", ["All"] + STATUSES)

    conn = get_connection()
    if conn:
        cur = conn.cursor(dictionary=True)
        query = "SELECT * FROM fir_records WHERE 1=1"
        params = []
        if filter_crime != "All":
            query += " AND crime_type=%s"; params.append(filter_crime)
        if filter_status != "All":
            query += " AND status=%s"; params.append(filter_status)
        query += " ORDER BY registered_at DESC"
        cur.execute(query, params)
        rows = cur.fetchall()
        cur.close(); conn.close()

        if rows:
            st.success(f"Found **{len(rows)}** FIR record(s)")
            import pandas as pd
            df = pd.DataFrame(rows)
            df.columns = ["ID", "FIR No", "Complainant", "CNIC", "Contact",
                          "Crime Type", "Location", "Incident Date",
                          "Description", "Status", "Officer", "Registered At"]
            st.dataframe(df, use_container_width=True)
        else:
            st.info("No FIR records found.")

# ── UPDATE ────────────────────────────────────────────────────────────────────
elif operation == " pdate FIR Status":
    st.subheader(" Update FIR Status")

    fir_id     = st.number_input("Enter FIR Record ID", min_value=1, step=1, key="upd_id")
    new_status = st.selectbox("New Status", STATUSES, key="upd_status")
    new_officer= st.text_input("Reassign Officer (leave blank to keep current)", key="upd_officer")

    if st.button(" Update FIR", key="update_btn"):
        conn = get_connection()
        if conn:
            cur = conn.cursor()
            if new_officer.strip():
                cur.execute(
                    "UPDATE fir_records SET status=%s, officer_name=%s WHERE id=%s",
                    (new_status, new_officer.strip(), int(fir_id))
                )
            else:
                cur.execute(
                    "UPDATE fir_records SET status=%s WHERE id=%s",
                    (new_status, int(fir_id))
                )
            conn.commit()
            if cur.rowcount:
                st.success(f" FIR ID {int(fir_id)} updated to **{new_status}**")
            else:
                st.error(" No FIR found with that ID.")
            cur.close(); conn.close()

# ── DELETE ────────────────────────────────────────────────────────────────────
elif operation == " Delete FIR Record":
    st.subheader(" Delete a FIR Record")
    st.warning("⚠️ This action is permanent and cannot be undone.")

    fir_id = st.number_input("Enter FIR Record ID to Delete", min_value=1, step=1, key="del_id")

    if st.button(" Confirm Delete", key="delete_btn"):
        conn = get_connection()
        if conn:
            cur = conn.cursor()
            cur.execute("DELETE FROM fir_records WHERE id=%s", (int(fir_id),))
            conn.commit()
            if cur.rowcount:
                st.success(f" FIR Record ID {int(fir_id)} has been permanently deleted.")
            else:
                st.error(" No FIR record found with that ID.")
            cur.close(); conn.close()

st.markdown("---")
st.caption("Punjab Police Department — FIR Digital Record System | DevOps for Cloud Computing — Spring 2026")
