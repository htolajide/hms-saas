import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Pill, Plus, ArrowLeft, Loader2, Trash2 } from 'lucide-react';
import { getHospitalId } from '../utils/auth';

export default function PharmacyManagement() {
    const navigate = useNavigate();
    const hospitalId = getHospitalId();
    
    const [meds, setMeds] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [masters, setMasters] = useState([]);
    const [selectedMasterId, setSelectedMasterId] = useState('');
    
    // Only stock-related fields needed since master provides name/category/price
    const [stockData, setStockData] = useState({ stockLevel: 0, reorderLevel: 10 });

    // Fetch inventory list
    const fetchData = async () => {
        try {
            const res = await api.get(`/pharmacy/medications?hospitalId=${hospitalId}`);
            setMeds(res.data);
        } catch (err) { console.error(err); } finally { setLoading(false); }
    };

    // Fetch master catalog when form opens
    useEffect(() => {
        if (showForm) {
            api.get(`/pharmacy/masters?hospitalId=${hospitalId}`)
                .then(res => setMasters(res.data))
                .catch(err => console.error("Failed to load masters", err));
        }
    }, [showForm]);

    useEffect(() => { fetchData(); }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedMasterId) return alert("Please select a medication");
        
        await api.post('/pharmacy/medications', {
            hospitalId,
            masterId: Number(selectedMasterId),
            ...stockData
        });
        
        setShowForm(false);
        setSelectedMasterId('');
        setStockData({ stockLevel: 0, reorderLevel: 10 });
        fetchData();
    };

    const handleDelete = async (id) => {
        if (window.confirm("Delete this inventory record?")) {
            await api.delete(`/pharmacy/medications/${id}`);
            fetchData();
        }
    };

    return (
        <div className="space-y-6">
            <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-500 hover:text-primary transition text-sm">
                <ArrowLeft className="h-4 w-4" /> Back to Dashboard
            </button>
            
            <div className="flex justify-between items-center">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
                        <div className="bg-purple-100 p-2 rounded-lg"><Pill className="h-7 w-7 text-purple-600" /></div>
                        Pharmacy & Inventory
                    </h2>
                    <p className="text-gray-500 mt-2 ml-12">Manage drugs, stock levels, and dispensing.</p>
                </div>
                <button onClick={() => setShowForm(!showForm)} className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium">
                    <Plus className="h-5 w-5" /> Add Stock
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl shadow-sm grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Select from Master Catalog */}
                    <div className="md:col-span-3">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Select Medication from Catalog</label>
                        <select 
                            value={selectedMasterId} 
                            onChange={e => setSelectedMasterId(e.target.value)}
                            required
                            className="w-full px-3 py-2 border bg-white rounded-lg focus:ring-2 focus:ring-purple-500 outline-none"
                        >
                            <option value="">Search and select medication...</option>
                            {masters.map(m => (
                                <option key={m.id} value={m.id}>
                                    {m.genericName} {m.strength} - {m.brandName} ({m.dosageForm}) | ₦{m.unitPrice}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Current Stock Level</label>
                        <input type="number" min="0" value={stockData.stockLevel} 
                            onChange={e => setStockData({...stockData, stockLevel: Number(e.target.value)})} 
                            className="w-full px-3 py-2 border bg-white rounded-lg focus:ring-2 focus:ring-purple-500 outline-none" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Reorder Threshold</label>
                        <input type="number" min="0" value={stockData.reorderLevel} 
                            onChange={e => setStockData({...stockData, reorderLevel: Number(e.target.value)})} 
                            className="w-full px-3 py-2 border bg-white rounded-lg focus:ring-2 focus:ring-purple-500 outline-none" />
                    </div>
                    <div className="flex items-end">
                        <button type="submit" className="w-full bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-700 transition font-medium">
                            Save Stock Record
                        </button>
                    </div>
                </form>
            )}

            {loading ? <Loader2 className="animate-spin mx-auto" /> : (
                <div className="bg-white rounded-2xl overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50/50">
                            <tr>
                                <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Medication</th>
                                <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Category</th>
                                <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Price</th>
                                <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Stock</th>
                                <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {meds.map(med => (
                                <tr key={med.id} className="hover:bg-gray-50/50">
                                    <td className="px-6 py-4">
                                        <p className="font-medium text-gray-900">{med.master?.genericName} {med.master?.strength}</p>
                                        <p className="text-xs text-gray-500">{med.master?.brandName} ({med.master?.dosageForm})</p>
                                    </td>
                                    {/* Display category label from master */}
                                    <td className="px-6 py-4 text-gray-600">{med.categoryLabel}</td>
                                    <td className="px-6 py-4 text-gray-600">₦{med.master?.unitPrice?.toLocaleString()}</td>
                                    <td className="px-6 py-4">
                                        <span className={`px-2 py-1 rounded-full text-xs font-bold ${
                                            med.stockLevel <= med.reorderLevel ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
                                        }`}>
                                            {med.stockLevel} units
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <button onClick={() => handleDelete(med.id)} className="text-red-600 hover:bg-red-50 p-2 rounded-lg">
                                            <Trash2 className="h-4 w-4" />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}