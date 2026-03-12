# London Gift Shop – Retail Data Analytics

# Introduction

London Gift Shop (LGS) is an online retail business that sells gift items to customers across multiple countries. As the business grows, LGS collects a large volume of transactional data from daily sales activities. While this data records every purchase, it does not directly explain customer behavior, customer value, or how different customers contribute to revenue.

The objective of this project is to transform raw transaction data into **clear and actionable customer insights**. By analyzing historical sales data, LGS can better understand how customers behave, identify high-value and loyal customers, and detect customers who are becoming inactive.

LGS can use the analytics results from this project to:
- Identify their most valuable and loyal customers
- Focus retention efforts on high-impact customer segments
- Design targeted marketing campaigns instead of broad promotions
- Allocate marketing and business resources more effectively

This project was implemented using **Python in Jupyter Notebook**, with data sourced from a **PostgreSQL database** and analyzed using common data analytics libraries such as **Pandas, NumPy, and Matplotlib**.

---

# Implementation

## Project Architecture

This project follows a simple analytics architecture similar to real-world data engineering workflows.

- The LGS web application stores transactional data (invoices, customers, products, prices, quantities, and timestamps) in a **PostgreSQL database**.
- The database acts as the **source of truth** for all sales data.
- Data is extracted from PostgreSQL into Python using **SQLAlchemy**.
- Data analytics and aggregation are performed using **Pandas** in a **Jupyter Notebook**.
- Insights are generated through summary tables and visualizations.

**High-level flow:**

👉**[Architecture Diagram](./analytics/Architecture%20diagram.png)**


## Data Analytics and Wrangling

The complete analysis is implemented in the following notebook:

👉 **[Retail Data Analytics Notebook](./analytics/london_giftshop_analysis.ipynb)**


### Analytics Performed

- Transaction data was cleaned and prepared for analysis.
- Sales data was aggregated at the **customer level**.
- **RFM (Recency, Frequency, Monetary)** metrics were calculated:
  - **Recency:** How recently a customer made a purchase
  - **Frequency:** How often a customer made purchases
  - **Monetary:** How much a customer spent in total
- Quantile-based scoring was used to fairly compare customers.
- Customers were grouped into meaningful **RFM segments** such as:
  - Champions
  - Loyal Customers
  - Can’t Lose
  - At Risk
  - Hibernating
  - Other supporting segments

### How LGS Can Use These Insights

Using RFM segmentation, LGS can:
- Focus retention efforts on **Champions and Loyal Customers**
- Identify **high-value inactive customers** and apply targeted win-back campaigns
- Avoid overspending on large inactive customer groups with low revenue potential
- Design personalized marketing strategies based on customer behavior rather than assumptions

This allows LGS to shift from **transaction-based analysis** to **customer-centric decision-making**.

---

# Improvements

If more time were available, the following improvements could be made:

1. **Customer-Level Campaign Analysis**  
   Track how different customer segments respond to marketing campaigns and promotions.

2. **Sales Forecasting**  
   Apply time-series forecasting models to predict future sales and seasonal demand.

3. **Automated Analytics Pipeline**  
   Convert the notebook-based workflow into a scheduled data pipeline with automated data refresh and reporting.

---
# Future Work
   - Convert the notebook-based workflow into a scheduled data pipeline
